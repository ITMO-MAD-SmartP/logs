package com.example

import com.ecwid.clickhouse.mapped.ClickHouseMappedClient
import com.ecwid.clickhouse.transport.HttpTransport
import com.ecwid.clickhouse.transport.httpclient.ApacheHttpClientTransport
import com.ecwid.clickhouse.typed.TypedRow
import com.ecwid.clickhouse.typed.TypedValues
import com.example.plugins.*
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.server.application.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import model.GetTLogsRequest
import model.GetTLogsResponseNode
import model.TLog
import model.TLogsResponse
import redis.clients.jedis.Jedis
import java.sql.Timestamp
import java.util.*


@OptIn(DelicateCoroutinesApi::class)
fun main() {
    val jedis = Jedis("localhost", 6379)
    val jedis1 = Jedis("localhost", 6379)
    val jedis2 = Jedis("localhost", 6379)
    val httpTransport: HttpTransport = ApacheHttpClientTransport()
    val clickHouseClient = ClickHouseMappedClient(httpTransport)
    val objectMapper = jacksonObjectMapper()

    val logToTypedValues: (TLog) -> TypedValues = { log ->
        val result = TypedValues()
        result.setInt32("sensorId",log.sensorId)
        result.setString("type", log.type)
        result.setFloat64("value", log.value)
        result.setDateTime("time", Date.from(log.time.toInstant()), TimeZone.getDefault())
        result
    }

    val typedRowToTLog: (TypedRow) -> TLog = { row ->
        val sensorId = row.getInt32("sensorId")
        val type = row.getString("type")
        val value = row.getFloat64("value")
        val time = Timestamp.from(row.getDateTime("time").toInstant())
        TLog(sensorId, type, value, time)
    }

    GlobalScope.launch {
        while (true) {
            val jsonTLogString = jedis.rpop("queue:tlog")
            if (!jsonTLogString.isNullOrEmpty()){
                val tLog: TLog = objectMapper.readValue(jsonTLogString)
                clickHouseClient.insert("http://localhost:8123", "temp_log", listOf(tLog), logToTypedValues)
                println("insert tlog to clickhouse. sensorId: " + tLog.sensorId)
            }
            delay(100)
        }
    }

    GlobalScope.launch {
        while (true) {
            val jsonTLogString = jedis1.rpop("queue:get:tlogs")
            if (!jsonTLogString.isNullOrEmpty()){
                println(jsonTLogString)

                val request: GetTLogsRequest = objectMapper.readValue(jsonTLogString)
                val sql = "select * from temp_log where sensorId = " + request.sensorId
                val list = mutableListOf<GetTLogsResponseNode>()

                clickHouseClient.select("http://localhost:8123", sql, typedRowToTLog).use { response ->
                    for (log in response) {
                        val node = GetTLogsResponseNode(log.time, log.value)
                        list.add(node)
                    }
                }
                val tLogsResponse: TLogsResponse = TLogsResponse(request.requestId, list)
                val stringList = objectMapper.writeValueAsString(tLogsResponse)
                jedis2.lpush("queue:tlogs-responses", stringList)
                println("sensor history sent for requestId = " + request.requestId)
                println(tLogsResponse)
            }
            delay(100)
        }
    }
    readLine()
}

fun Application.module() {
    configureHTTP()
}


