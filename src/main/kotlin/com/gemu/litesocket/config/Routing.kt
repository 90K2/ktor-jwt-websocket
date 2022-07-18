package com.gemu.litesocket.config

import io.ktor.network.sockets.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import java.util.*


fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Pong")
        }
        val connections = Collections.synchronizedMap<String, DefaultWebSocketSession>(mutableMapOf())

        try {
            authenticate("auth-jwt") {
                webSocket("/private") {
                    val principal = call.principal<JWTPrincipal>()
                    val username = principal?.payload?.getClaim("sub")?.asString() ?: throw RuntimeException("Username not found")

                    val connect = connections[username] ?: run {
                        connections[username] = this
                        this
                    }
                    send("You've logged in as [$username]")

//                    for (frame in incoming) {
//                        when (frame) {
//                            is Frame.Text -> {
//                                val receivedText = frame.readText()
//                                val textWithUsername = "[${thisConnection.name}]: $receivedText"
//                                connections.forEach {
//                                    it.session.send(textWithUsername)
//                                }
//                            }
//                            is Frame.Binary -> TODO()
//                            is Frame.Close -> TODO()
//                            is Frame.Ping -> TODO()
//                            is Frame.Pong -> TODO()
//                        }
//                    }
                }
            }
        } catch (ex: Exception) {
            println(ex)
        }
    }
}
