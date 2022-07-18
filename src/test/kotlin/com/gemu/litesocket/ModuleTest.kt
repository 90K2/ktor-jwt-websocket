package com.gemu.litesocket

import com.auth0.jwt.JWT
import com.gemu.litesocket.config.RS256Algorithm
import com.gemu.litesocket.config.Security
import com.gemu.litesocket.redis.RedisFactory
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.testing.*
import io.ktor.websocket.*
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import kotlin.test.*
import io.lettuce.core.codec.StringCodec

class ModuleTest {

    @Test
    fun testJwtVerifier() {
        val (private, public) = Security().readPrivateKey()
        val token = "eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJzZW5zaHVfNjU1MTc4NjdfeTFTaTAiLCJpc1Rlc3QiOmZhbHNlLCJpc3MiOiJodHRwczpcL1wvb2EudG9ucGxheS5pbyIsImV4cCI6MTY2NjUyMTIwOSwidG9rZW5fdHlwZSI6IkFOT05ZTSIsImF1dGhvcml0aWVzIjpbIlJPTEVfVVNFUiJdfQ.EMQT7Wy52xGUqtGb_8RciJT8_z8MV20uBElxPcNnLMF15GwoZSAnuxCrBg4i2kIYT3owuLOoFVi5eRDxNrw6r4fqTamXXLqRQM7Wo_DiWfo0O8lGx8bWO6Gyru2-LfANerV66Szd_-pNumhZBk32dJ3dDnVYkM0ROE9_A_V-b2yMh8JNwoXXSfXKp48F39nRYcLvKOmeBlcxEwn0hgY_I8MdXQr2-lInoV-xf2jz7cTaZiNaXdNF9QuKG8hD03dW2otG54T98vLb21gvz8EtymYkoYz_jlHg_GZzOuLCKuaftpkS0asxH5gchgxCoCCwZEvLLY10ZQJTd8UC52sT"
        val verifier = JWT
            .require(
                RS256Algorithm(private as RSAPrivateKey, public as RSAPublicKey)
            )
            .build()

        val res = verifier.verify(token)
        println()
    }

    @Test
    fun testRedis() {
        testApplication {

            val client = RedisFactory.newClient(StringCodec.UTF8)
            println(client.set("somekey","somevalue"))
            println(client.get("somekey"))
        }

    }

    @Test
    fun testApp() {
        testApplication {
            val client = createClient {
                install(WebSockets)
            }

            try {
                client.webSocket("/private", {
                    header(
                        HttpHeaders.Authorization,
                        "Bearer 123"
                    )
                }) {
                    val greetingText = (incoming.receive() as? Frame.Text)?.readText() ?: ""
                    assertEquals("You've logged in as [user0]", greetingText)

                    send(Frame.Text("Hello, I was first!"))
                    val responseText = (incoming.receive() as Frame.Text).readText()
                    assertEquals("[user0]: Hello, I was first!", responseText)
                }
            } catch (ex: NullPointerException) {
                println("Something went wrong. Cannot connect to socket channel or smth else\n${ex.stackTraceToString()}")
                throw ex
            }
        }
    }
}
