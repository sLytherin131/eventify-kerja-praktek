package com.eventify.routes

import com.eventify.models.PersonalTask
import com.eventify.repository.PersonalTaskRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

fun Application.registerPersonalTaskRoutes() {
    val repository = PersonalTaskRepository()

    routing {
        authenticate("auth-jwt") {
            route("/personal_tasks") {

                // Get all tasks for logged-in admin
                get {
                    val principal = call.principal<JWTPrincipal>()!!
                    val adminWhatsapp = principal.payload.getClaim("whatsappNumber").asString()
                    val tasks = repository.getAllByAdmin(adminWhatsapp)
                    call.respond(HttpStatusCode.OK, tasks)
                }

                // Get a specific task by id (only if belongs to user)
                get("{id}") {
                    val id = call.parameters["id"]?.toIntOrNull()
                    val principal = call.principal<JWTPrincipal>()!!
                    val adminWhatsapp = principal.payload.getClaim("whatsappNumber").asString()

                    if (id == null) {
                        call.respond(HttpStatusCode.BadRequest, "Invalid task id")
                        return@get
                    }

                    val task = repository.getById(id)
                    if (task == null || task.adminWhatsapp != adminWhatsapp) {
                        call.respond(HttpStatusCode.NotFound, "Task not found or you are not authorized to view it")
                    } else {
                        call.respond(HttpStatusCode.OK, task)
                    }
                }

                // Create a new personal task
                post {
                    val principal = call.principal<JWTPrincipal>()!!
                    val adminWhatsapp = principal.payload.getClaim("whatsappNumber").asString()
                    val request = call.receive<PersonalTaskRequest>()

                    val task = PersonalTask(
                        adminWhatsapp = adminWhatsapp,
                        description = request.description,
                        taskType = request.taskType
                    )
                    val createdTask = repository.create(task)
                    if (createdTask != null) {
                        call.respond(HttpStatusCode.Created, createdTask)
                    } else {
                        call.respond(HttpStatusCode.InternalServerError, "Failed to create task")
                    }
                }

                // Update an existing task
                put("{id}") {
                    val id = call.parameters["id"]?.toIntOrNull()
                    val principal = call.principal<JWTPrincipal>()!!
                    val adminWhatsapp = principal.payload.getClaim("whatsappNumber").asString()

                    if (id == null) {
                        call.respond(HttpStatusCode.BadRequest, "Invalid task id")
                        return@put
                    }

                    val request = call.receive<UpdatePersonalTaskRequest>()
                    val existingTask = repository.getById(id)

                    if (existingTask == null || existingTask.adminWhatsapp != adminWhatsapp) {
                        call.respond(HttpStatusCode.NotFound, "Task not found or you are not authorized to update it")
                        return@put
                    }

                    val updatedTask = existingTask.copy(
                        description = request.description ?: existingTask.description,
                        taskType = request.taskType ?: existingTask.taskType
                    )

                    if (repository.update(updatedTask)) {
                        call.respond(HttpStatusCode.OK, updatedTask)
                    } else {
                        call.respond(HttpStatusCode.InternalServerError, "Failed to update task")
                    }
                }

                // Delete a task by id
                delete("{id}") {
                    val id = call.parameters["id"]?.toIntOrNull()
                    val principal = call.principal<JWTPrincipal>()!!
                    val adminWhatsapp = principal.payload.getClaim("whatsappNumber").asString()

                    if (id == null) {
                        call.respond(HttpStatusCode.BadRequest, "Invalid task id")
                        return@delete
                    }

                    val task = repository.getById(id)
                    if (task == null || task.adminWhatsapp != adminWhatsapp) {
                        call.respond(HttpStatusCode.NotFound, "Task not found or you are not authorized to delete it")
                        return@delete
                    }

                    if (repository.delete(id)) {
                        call.respond(HttpStatusCode.OK, "Task deleted successfully")
                    } else {
                        call.respond(HttpStatusCode.InternalServerError, "Failed to delete task")
                    }
                }
            }
        }
    }
}

@Serializable
data class PersonalTaskRequest(
    val description: String,
    val taskType: String
)

@Serializable
data class UpdatePersonalTaskRequest(
    val description: String? = null,
    val taskType: String? = null
)
