package com.eventify.routes

import com.eventify.models.Admin
import com.eventify.repository.AdminRepository
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.http.*

fun Application.registerAdminRoutes() {
    val adminRepository = AdminRepository()

    routing {
        route("/admin") {
            post {
                val adminRequest = call.receive<AdminRequest>()
                val existingAdmin = adminRepository.findByWhatsappNumber(adminRequest.whatsappNumber)
                if (existingAdmin != null) {
                    call.respond(HttpStatusCode.Conflict, "Admin with this WhatsApp number already exists")
                    return@post
                }
                val admin = Admin(
                    whatsappNumber = adminRequest.whatsappNumber,
                    name = adminRequest.name,
                    email = adminRequest.email,
                    password = adminRequest.password
                )
                val created = adminRepository.create(admin)
                if (created) {
                    call.respond(HttpStatusCode.Created, "Admin created successfully")
                } else {
                    call.respond(HttpStatusCode.InternalServerError, "Failed to create admin")
                }
            }

            post("/login") {
                val loginRequest = call.receive<LoginRequest>()
                val admin = adminRepository.findByWhatsappNumber(loginRequest.whatsappNumber)
                if (admin == null) {
                    call.respond(HttpStatusCode.Unauthorized, "Invalid WhatsApp number or password")
                    return@post
                }
                val passwordValid = adminRepository.verifyPassword(loginRequest.password, admin.password)
                if (!passwordValid) {
                    call.respond(HttpStatusCode.Unauthorized, "Invalid WhatsApp number or password")
                    return@post
                }
                call.respond(HttpStatusCode.OK, "Login successful")
            }
        }
    }
}

@kotlinx.serialization.Serializable
data class AdminRequest(
    val whatsappNumber: String,
    val name: String,
    val email: String,
    val password: String
)

@kotlinx.serialization.Serializable
data class LoginRequest(
    val whatsappNumber: String,
    val password: String
)
