package com.eventify

import com.eventify.routes.registerAdminRoutes
import com.eventify.routes.registerPersonalTaskRoutes
import com.eventify.routes.registerMemberRoutes
import com.eventify.database.DatabaseFactory
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*

fun main() {
    embeddedServer(Netty, port = 8082, host = "0.0.0.0") {
        configureSerialization()
        DatabaseFactory.init()
        registerAdminRoutes()
        registerPersonalTaskRoutes() // Newly added endpoint for personal tasks
        registerMemberRoutes() // Newly added endpoint for members
    }.start(wait = true)
}

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json()
    }
}
