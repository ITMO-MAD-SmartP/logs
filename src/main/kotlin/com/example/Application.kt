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
import model.TLog
import redis.clients.jedis.Jedis

@OptIn(DelicateCoroutinesApi::class)
fun main() {
    val jedis = Jedis("localhost", 6379)
    val httpTransport: HttpTransport = ApacheHttpClientTransport()
    val clickHouseClient = ClickHouseMappedClient(httpTransport)
    val objectMapper = jacksonObjectMapper()

    val logToTypedValues: (TLog) -> TypedValues = { log ->
        val result = TypedValues()
        result.setInt32("sensorId",log.sensorId)
        result.setString("type", log.type)
        result.setFloat64("value", log.value)
        result
    }

    GlobalScope.launch {
        while (true) {
            val jsonTLogString = jedis.rpop("queue:tlogs")
            if (!jsonTLogString.isNullOrEmpty()){
                val tLog: TLog = objectMapper.readValue(jsonTLogString)
                clickHouseClient.insert("http://localhost:8123", "temp_log", listOf(tLog), logToTypedValues)
            }
            delay(1)
        }
    }
    readLine()
}

fun Application.module() {
    configureHTTP()
    configureRouting()
}


