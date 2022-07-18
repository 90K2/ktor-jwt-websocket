package com.gemu.litesocket

import com.gemu.litesocket.config.configureRouting
import com.gemu.litesocket.config.configureSecurity
import com.gemu.litesocket.redis.RedisFactory
import io.ktor.server.application.*
import io.ktor.server.websocket.*
import io.ktor.server.auth.*
import java.time.Duration


fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused")
fun Application.module() {
    val redisHost = environment.config.property("redis.host").getString()
    val redisPort = environment.config.property("redis.port").getString()

    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }
    install(RedisFactory) {
        url = "redis://$redisHost:$redisPort/0?timeout=10s"
    }
    install(Authentication)
    configureSecurity()
    configureRouting()
}
