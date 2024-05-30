package com.example

import redis.clients.jedis.Jedis

class RedisClient(private val host: String, private val port: Int) {
    private val jedis: Jedis = Jedis(host, port)


}