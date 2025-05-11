package com.eventify

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import com.eventify.routes.registerAdminRoutes
import com.eventify.database.DatabaseFactory

fun main() {
    embeddedServer(Netty, port = 8082, host = "0.0.0.0") {
        configureSerialization()
        DatabaseFactory.init()
        registerAdminRoutes()
    }.start(wait = true)
}

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json()
    }
}
