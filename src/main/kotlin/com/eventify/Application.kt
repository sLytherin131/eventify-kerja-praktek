package com.eventify

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.eventify.database.DatabaseFactory
import com.eventify.routes.registerAdminRoutes
import com.eventify.routes.registerEventRoutes
import com.eventify.routes.registerMemberRoutes
import com.eventify.routes.registerPersonalTaskRoutes
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*

fun main() {
    embeddedServer(Netty, port = 8082, host = "0.0.0.0") {
        configureSerialization()
        configureSecurity() // JWT setup
        DatabaseFactory.init()
        registerAdminRoutes()
        registerMemberRoutes()
        registerPersonalTaskRoutes()
        registerEventRoutes()
    }.start(wait = true)
}

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json()
    }
}

fun Application.configureSecurity() {
    install(Authentication) {
        jwt("auth-jwt") {
            realm = "eventify"
            verifier(
                JWT.require(Algorithm.HMAC256("secret")) // Ganti secret dengan env var di produksi
                    .withAudience("eventify-users")
                    .withIssuer("eventify")
                    .build()
            )
            validate { credential ->
                if (credential.payload.getClaim("whatsappNumber").asString().isNotEmpty()) {
                    JWTPrincipal(credential.payload)
                } else null
            }
        }
    }
}
