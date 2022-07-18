package com.gemu.litesocket.config

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import com.nimbusds.jose.jwk.RSAKey
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import java.security.PrivateKey
import java.security.PublicKey
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey


class Security {
    fun readPrivateKey(): Pair<PrivateKey, PublicKey> {
        val rsaJWKkp = RSAKey.parse(this::class.java.getResource("/private.pk")?.readText())
            .toKeyPair()
        return Pair(rsaJWKkp.private, rsaJWKkp.public)
    }
}

class RS256Algorithm(
    privateKey: RSAPrivateKey, publicKey: RSAPublicKey
): Algorithm("RS256", "") {

    override fun verify(jwt: DecodedJWT?) {

    }

    override fun sign(contentBytes: ByteArray?): ByteArray {
        TODO("Not yet implemented")
    }

}

fun Application.configureSecurity() {
    val issuer = environment.config.property("jwt.issuer").getString()

    authentication {
        jwt("auth-jwt") {
            val (private, public) = Security().readPrivateKey()
            verifier(
                JWT
                    .require(
                        RS256Algorithm(private as RSAPrivateKey, public as RSAPublicKey)
                    )
                    .build()
            )
            validate { credential ->
                if (credential.payload.issuer != issuer) throw RuntimeException("Wrong JWT issuer")

                if (credential.payload.getClaim("sub")?.asString() != "") {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
//            challenge { _, _ ->
//                call.respond(HttpStatusCode.Unauthorized)
//            }
        }
    }
}
