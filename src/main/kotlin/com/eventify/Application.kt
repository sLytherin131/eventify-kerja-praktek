package com.eventify

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.eventify.database.DatabaseFactory
import com.eventify.repository.EventRepository
import com.eventify.routes.*
import com.eventify.service.WablasService
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.*
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

fun main() {
    embeddedServer(Netty, port = 8082, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

// Modul utama Ktor
fun Application.module() {
    configureSerialization()
    configureSecurity()
    DatabaseFactory.init()

    registerAdminRoutes()
    registerMemberRoutes()
    registerPersonalTaskRoutes()
    registerEventRoutes()

    // ✅ Pindahkan scheduler ke sini agar tidak error saat build/deploy
    launchReminderScheduler()
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
                JWT.require(Algorithm.HMAC256("secret"))
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

fun Application.launchReminderScheduler() {
    val eventRepository = EventRepository()

    CoroutineScope(Dispatchers.IO).launch {
        while (true) {
            try {
                val now = Instant.now().toEpochMilli()
                val threeDaysFromNow = now + 3 * 24 * 60 * 60 * 1000

                val events = eventRepository.getEventsForReminder(threeDaysFromNow)

                for (event in events) {
                    val message = buildString {
                        appendLine("📢 *Pengingat Acara!*")
                        appendLine("Acara: ${event.event.name}")
                        appendLine("Tanggal: ${formatDateTime(event.event.startTime)}")
                        appendLine("Deskripsi: ${event.event.description}")
                        appendLine()
                        appendLine("Jangan lupa untuk hadir ya! 🙌")
                    }

                    event.members.forEach { member ->
                        WablasService.sendMessage(
                            phone = member.memberWhatsapp,
                            message = message
                        )
                    }

                    eventRepository.markReminderSent(event.event.id)
                }
            } catch (e: Exception) {
                println("Reminder Scheduler Error: ${e.message}")
            }

            delay(6 * 60 * 60 * 1000L) // setiap 6 jam
        }
    }
}

fun formatDateTime(epochMillis: Long): String {
    val formatter = DateTimeFormatter.ofPattern("EEEE – HH:mm, dd MMMM yyyy")
        .withLocale(Locale("id", "ID"))
        .withZone(ZoneId.of("Asia/Jakarta"))
    return formatter.format(Instant.ofEpochMilli(epochMillis))
}
