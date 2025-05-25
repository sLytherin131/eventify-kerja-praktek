package com.eventify.routes

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.eventify.models.Admin
import com.eventify.repository.AdminRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

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

            get {
                val admins = adminRepository.getAll()
                call.respond(HttpStatusCode.OK, admins)
            }

            get("/{whatsappNumber}") {
                val whatsappNumber = call.parameters["whatsappNumber"]
                if (whatsappNumber == null) {
                    call.respond(HttpStatusCode.BadRequest, "WhatsApp number is required")
                    return@get
                }
                val admin = adminRepository.findByWhatsappNumber(whatsappNumber)
                if (admin == null) {
                    call.respond(HttpStatusCode.NotFound, "Admin not found")
                } else {
                    call.respond(admin)
                }
            }

            put("/{whatsappNumber}") {
                val whatsappNumber = call.parameters["whatsappNumber"]
                if (whatsappNumber == null) {
                    call.respond(HttpStatusCode.BadRequest, "WhatsApp number is required")
                    return@put
                }
                val updateRequest = call.receive<AdminRequest>()
                val updated = adminRepository.updateAdmin(whatsappNumber, Admin(
                    whatsappNumber = whatsappNumber,
                    name = updateRequest.name,
                    email = updateRequest.email,
                    password = updateRequest.password,
                    createdAt = System.currentTimeMillis()
                ))
                if (updated) {
                    call.respond(HttpStatusCode.OK, "Admin updated successfully")
                } else {
                    call.respond(HttpStatusCode.NotFound, "Admin not found or update failed")
                }
            }

            delete("/{whatsappNumber}") {
                val whatsappNumber = call.parameters["whatsappNumber"]
                if (whatsappNumber == null) {
                    call.respond(HttpStatusCode.BadRequest, "WhatsApp number is required")
                    return@delete
                }
                val deleted = adminRepository.deleteAdmin(whatsappNumber)
                if (deleted) {
                    call.respond(HttpStatusCode.OK, "Admin deleted successfully")
                } else {
                    call.respond(HttpStatusCode.NotFound, "Admin not found or deletion failed")
                }
            }

            post("/login") {
                val loginRequest = call.receive<LoginRequest>()
                val admin = adminRepository.findByWhatsappNumber(loginRequest.identifier)
                    ?: adminRepository.findByEmail(loginRequest.identifier)
                if (admin == null) {
                    call.respond(HttpStatusCode.Unauthorized, "Invalid WhatsApp number/email or password")
                    return@post
                }
                val passwordValid = adminRepository.verifyPassword(loginRequest.password, admin.password)
                if (!passwordValid) {
                    call.respond(HttpStatusCode.Unauthorized, "Invalid WhatsApp number/email or password")
                    return@post
                }
                val token = generateToken(admin)
                call.respond(HttpStatusCode.OK, mapOf("token" to token))
            }

            authenticate("auth-jwt") {
                get("/me") {
                    val principal = call.principal<JWTPrincipal>()
                    val whatsappNumber = principal!!.payload.getClaim("whatsappNumber").asString()

                    val admin = adminRepository.findByWhatsappNumber(whatsappNumber)
                    if (admin == null) {
                        call.respond(HttpStatusCode.NotFound, "Admin not found")
                    } else {
                        call.respond(admin)
                    }
                }
            }
        }
    }
}

fun generateToken(admin: Admin): String {
    return JWT.create()
        .withAudience("eventify-users")
        .withIssuer("eventify")
        .withClaim("whatsappNumber", admin.whatsappNumber)
        .withClaim("email", admin.email)
        .sign(Algorithm.HMAC256("secret")) // Ganti dengan kunci rahasia yang aman di environment variable
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
    val identifier: String, // bisa whatsapp number atau email
    val password: String
)
