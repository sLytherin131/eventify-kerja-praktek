package com.eventify.routes

import com.eventify.models.PersonalTask
import com.eventify.repository.PersonalTaskRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

fun Application.registerPersonalTaskRoutes() {
    val repository = PersonalTaskRepository()

    routing {
        route("/personal_tasks") {
            // Get all tasks for an admin (e.g., ?admin_whatsapp=12345)
            get {
                val adminWhatsapp = call.request.queryParameters["admin_whatsapp"]
                if (adminWhatsapp.isNullOrEmpty()) {
                    call.respond(HttpStatusCode.BadRequest, "Admin WhatsApp number is required")
                    return@get
                }
                val tasks = repository.getAllByAdmin(adminWhatsapp)
                call.respond(HttpStatusCode.OK, tasks)
            }

            // Get a specific task by id
            get("{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid task id")
                    return@get
                }
                val task = repository.getById(id)
                if (task == null) {
                    call.respond(HttpStatusCode.NotFound, "Task not found")
                } else {
                    call.respond(HttpStatusCode.OK, task)
                }
            }

            // Create a new personal task
            post {
                val request = call.receive<PersonalTaskRequest>()
                val task = PersonalTask(
                    adminWhatsapp = request.adminWhatsapp,
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
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid task id")
                    return@put
                }
                val request = call.receive<UpdatePersonalTaskRequest>()
                val existingTask = repository.getById(id)
                if (existingTask == null) {
                    call.respond(HttpStatusCode.NotFound, "Task not found")
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
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid task id")
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

@Serializable
data class PersonalTaskRequest(
    val adminWhatsapp: String,
    val description: String,
    val taskType: String
)

@Serializable
data class UpdatePersonalTaskRequest(
    val description: String? = null,
    val taskType: String? = null
)
