package com.example.plugins

import com.ecwid.clickhouse.mapped.ClickHouseMappedClient
import com.ecwid.clickhouse.transport.HttpTransport
import com.ecwid.clickhouse.transport.httpclient.ApacheHttpClientTransport
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        get("/") {
            val httpTransport: HttpTransport = ApacheHttpClientTransport()
            val client = ClickHouseMappedClient(httpTransport)

            var a = 0
            val c = client.select<Int>(
                "http://localhost:8123",
                "SELECT count() FROM trips",
                {e -> e.getUInt16("count()")}).use {
                    resp -> for (user in resp) {
                System.out.println(user)
                a = user
            }
            }

            call.respondText(a.toString())
        }
    }
}
