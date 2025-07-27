package com.eventify.routes

import com.eventify.models.*
import com.eventify.repository.EventRepository
import com.eventify.service.WablasService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

fun formatDateTimeForEvent(epochMillis: Long): String {
    val formatter = DateTimeFormatter.ofPattern("EEEE – HH:mm, dd MMMM yyyy")
        .withLocale(Locale.ENGLISH)
        .withZone(ZoneId.of("Asia/Jakarta"))
    return formatter.format(Instant.ofEpochMilli(epochMillis))
}

fun Application.registerEventRoutes() {
    val eventRepository = EventRepository()

    routing {
        authenticate("auth-jwt") {
            route("/events") {

                // CREATE
                post {
                    val principal = call.principal<JWTPrincipal>()
                    val createdBy = principal!!.payload.getClaim("whatsappNumber").asString()

                    val request = call.receive<CreateEventRequest>()
                    if (request.name.isBlank()) {
                        call.respond(HttpStatusCode.BadRequest, "Missing required fields")
                        return@post
                    }

                    val validTaskTypes = setOf("normal", "urgent")
                    if (request.eventTasks.any { it.taskType !in validTaskTypes }) {
                        call.respond(HttpStatusCode.BadRequest, "Invalid task type. Must be 'normal' or 'urgent'")
                        return@post
                    }

                    val event = Event(
                        name = request.name,
                        description = request.description,
                        startTime = request.startTime,
                        endTime = request.endTime,
                        createdBy = createdBy
                    )

                    val tasks = request.eventTasks.map {
                        EventTask(
                            eventId = 0,
                            description = it.description,
                            taskType = it.taskType,
                            createdAt = it.createdAt
                        )
                    }

                    val members = request.eventMembers.map {
                        EventMember(
                            eventId = 0,
                            memberWhatsapp = it.memberWhatsapp
                        )
                    }

                    val createdEvent = eventRepository.createCompositeEvent(event, tasks, members)
                    if (createdEvent != null) {
                        GlobalScope.launch {
                            val message = buildString {
                                appendLine("📢 *Event Baru Telah Dibuat!*")
                                appendLine()
                                appendLine("${createdEvent.name}")
                                appendLine("${formatDateTimeForEvent(createdEvent.startTime)}")
                                appendLine("${formatDateTimeForEvent(createdEvent.endTime)}")
                                appendLine()
                                appendLine("${createdEvent.description}")
                                appendLine()
                                appendLine("Task:")
                                request.eventTasks.forEachIndexed { index, task ->
                                    appendLine("${index + 1}. ${task.description} – ${task.taskType.replaceFirstChar { it.uppercase() }}")
                                }
                            }

                            members.forEach { member ->
                                WablasService.sendMessage(
                                    phone = member.memberWhatsapp,
                                    message = message
                                )
                            }
                        }

                        call.respond(HttpStatusCode.Created, createdEvent)
                    } else {
                        call.respond(HttpStatusCode.InternalServerError, "Failed to create event")
                    }
                }

                // Tambahkan data class ini
                get {
                    val createdBy = call.request.queryParameters["createdBy"]
                    val events = if (createdBy != null) {
                        eventRepository.getEventsByCreatedBy(createdBy)
                    } else {
                        eventRepository.getAllEventsWithDetails()
                    }

                    // Langsung respond list-nya
                    call.respond(HttpStatusCode.OK, events)
                }

                // READ SINGLE EVENT
                get("/{id}") {
                    val id = call.parameters["id"]?.toIntOrNull()
                    if (id == null) {
                        call.respond(HttpStatusCode.BadRequest, "Invalid or missing ID")
                        return@get
                    }

                    val event = eventRepository.getEventWithDetailsById(id)
                    if (event != null) {
                        call.respond(HttpStatusCode.OK, event)
                    } else {
                        call.respond(HttpStatusCode.NotFound, "Event not found")
                    }
                }

                put("/{id}") {
                    val id = call.parameters["id"]?.toIntOrNull()
                    if (id == null) {
                        call.respond(HttpStatusCode.BadRequest, "Invalid ID")
                        return@put
                    }

                    val principal = call.principal<JWTPrincipal>()
                    val createdBy = principal!!.payload.getClaim("whatsappNumber").asString()

                    val request = call.receive<CreateEventRequest>()

                    val validTaskTypes = setOf("normal", "urgent")
                    if (request.eventTasks.any { it.taskType !in validTaskTypes }) {
                        call.respond(HttpStatusCode.BadRequest, "Invalid task type. Must be 'normal' or 'urgent'")
                        return@put
                    }

                    val updatedEvent = Event(
                        id = id,
                        name = request.name,
                        description = request.description,
                        startTime = request.startTime,
                        endTime = request.endTime,
                        createdBy = createdBy
                    )

                    val updatedTasks = request.eventTasks.map {
                        EventTask(
                            eventId = id,
                            description = it.description,
                            taskType = it.taskType,
                            createdAt = it.createdAt
                        )
                    }

                    val updatedMembers = request.eventMembers.map {
                        EventMember(
                            eventId = id,
                            memberWhatsapp = it.memberWhatsapp
                        )
                    }

                    val result = eventRepository.updateCompositeEvent(id, updatedEvent, updatedTasks, updatedMembers)
                    if (result) {
                        // Kirim ulang pesan ke semua member
                        GlobalScope.launch {
                            val message = buildString {
                                appendLine("📢 *Event Telah Diperbarui!*")
                                appendLine()
                                appendLine("${updatedEvent.name}")
                                appendLine("${formatDateTimeForEvent(updatedEvent.startTime)}")
                                appendLine("${formatDateTimeForEvent(updatedEvent.endTime)}")
                                appendLine()
                                appendLine("${updatedEvent.description}")
                                appendLine()
                                appendLine("Task:")
                                updatedTasks.forEachIndexed { index, task ->
                                    appendLine("${index + 1}. ${task.description} – ${task.taskType.replaceFirstChar { it.uppercase() }}")
                                }
                            }

                            updatedMembers.forEach { member ->
                                WablasService.sendMessage(
                                    phone = member.memberWhatsapp,
                                    message = message
                                )
                            }
                        }

                        call.respond(HttpStatusCode.OK, "Event updated successfully")
                    } else {
                        call.respond(HttpStatusCode.InternalServerError, "Failed to update event")
                    }
                }

                // DELETE EVENT
                delete("/{id}") {
                    val id = call.parameters["id"]?.toIntOrNull()
                    if (id == null) {
                        call.respond(HttpStatusCode.BadRequest, "Invalid ID")
                        return@delete
                    }

                    val result = eventRepository.deleteEventById(id)
                    if (result) {
                        call.respond(HttpStatusCode.OK, "Event deleted successfully")
                    } else {
                        call.respond(HttpStatusCode.InternalServerError, "Failed to delete event")
                    }
                }
            }
        }
    }
}

// Data classes for request
@Serializable
data class CreateEventRequest(
    val name: String,
    val description: String,
    val startTime: Long,
    val endTime: Long,
    val eventTasks: List<EventTaskRequest>,
    val eventMembers: List<EventMemberRequest>
)

@Serializable
data class EventTaskRequest(
    val description: String,
    val taskType: String,
    val createdAt: Long
)

@Serializable
data class EventMemberRequest(
    val memberWhatsapp: String
)
