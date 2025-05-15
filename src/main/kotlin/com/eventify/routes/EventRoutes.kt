package com.eventify.routes

import com.eventify.models.Event
import com.eventify.models.EventMember
import com.eventify.models.EventTask
import com.eventify.repository.EventRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

fun Application.registerEventRoutes() {
    val eventRepository = EventRepository()

    routing {
        route("/events") {
            post {
                val request = call.receive<CreateEventRequest>()

                // Basic validation (ensure required fields are present)
                if(request.name.isBlank() || request.createdBy.isBlank()){
                    call.respond(HttpStatusCode.BadRequest, "Missing required fields")
                    return@post
                }

                val event = Event(
                    name = request.name,
                    description = request.description,
                    startTime = request.startTime,
                    endTime = request.endTime,
                    createdBy = request.createdBy
                )
                // Prepare tasks and members from request
                val tasks = request.eventTasks.map {
                    EventTask(
                        eventId = 0, // will use the generated id
                        description = it.description,
                        taskType = it.taskType,
                        createdAt = it.createdAt
                    )
                }
                val members = request.eventMembers.map {
                    EventMember(
                        eventId = 0, // will be set in repository
                        memberWhatsapp = it.memberWhatsapp
                    )
                }

                val createdEvent = eventRepository.createCompositeEvent(event, tasks, members)
                if (createdEvent != null) {
                    call.respond(HttpStatusCode.Created, createdEvent)
                } else {
                    call.respond(HttpStatusCode.InternalServerError, "Failed to create event")
                }
            }
            // (Optional) You can add GET, PUT, DELETE endpoints here as needed.
        }
    }
}

@Serializable
data class CreateEventRequest(
    val name: String,
    val description: String,
    val startTime: Long,
    val endTime: Long,
    val createdBy: String,
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