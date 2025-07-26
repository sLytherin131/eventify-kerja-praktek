package com.eventify.routes

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.eventify.models.Admin
import com.eventify.repository.AdminRepository
import com.eventify.service.WablasService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import java.util.concurrent.ConcurrentHashMap

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
                val hashedPassword = adminRepository.hashPassword(adminRequest.password)
                val admin = Admin(
                    whatsappNumber = adminRequest.whatsappNumber,
                    name = adminRequest.name,
                    email = adminRequest.email,
                    password = hashedPassword
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
            
                // ✅ Hash password sebelum update
                val hashedPassword = adminRepository.hashPassword(updateRequest.password)
            
                val updated = adminRepository.updateAdmin(
                    whatsappNumber,
                    Admin(
                        whatsappNumber = whatsappNumber,
                        name = updateRequest.name,
                        email = updateRequest.email,
                        password = hashedPassword, // 👈 password sudah di-hash
                        createdAt = System.currentTimeMillis()
                    )
                )
            
                if (updated) {
                    val updatedAdmin = adminRepository.findByWhatsappNumber(whatsappNumber)
                    if (updatedAdmin != null) {
                        call.respond(HttpStatusCode.OK, updatedAdmin)
                    } else {
                        call.respond(HttpStatusCode.InternalServerError, "Updated but failed to retrieve admin")
                    }
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

            // ✅ SEND RESET CODE
            post("/send-reset-code") {
                val request = call.receive<Map<String, String>>()
                val identifier = request["identifier"]
                if (identifier.isNullOrBlank()) {
                    call.respond(HttpStatusCode.BadRequest, "Missing identifier")
                    return@post
                }

                val admin = adminRepository.findByWhatsappNumber(identifier) ?: adminRepository.findByEmail(identifier)
                if (admin == null) {
                    call.respond(HttpStatusCode.NotFound, "Admin tidak ditemukan")
                    return@post
                }

                val code = (100000..999999).random().toString()
                ResetCodeStorage.codes[identifier] = code

                WablasService.sendMessage(identifier, "Kode reset password Anda: $code")
                call.respond(HttpStatusCode.OK, "Kode verifikasi telah dikirim")
            }

            // ✅ RESET PASSWORD
            post("/reset-password") {
                val request = call.receive<Map<String, String>>()
                val identifier = request["identifier"]
                val code = request["code"]
                val newPassword = request["newPassword"]

                if (identifier.isNullOrBlank() || code.isNullOrBlank() || newPassword.isNullOrBlank()) {
                    call.respond(HttpStatusCode.BadRequest, "Semua field wajib diisi")
                    return@post
                }

                val validCode = ResetCodeStorage.codes[identifier]
                if (validCode != code) {
                    call.respond(HttpStatusCode.BadRequest, "Kode salah atau sudah kadaluarsa")
                    return@post
                }

                val admin = adminRepository.findByWhatsappNumber(identifier) ?: adminRepository.findByEmail(identifier)
                if (admin == null) {
                    call.respond(HttpStatusCode.NotFound, "Admin tidak ditemukan")
                    return@post
                }

                val hashedPassword = adminRepository.hashPassword(newPassword)
                val updated = adminRepository.updateAdmin(
                    admin.whatsappNumber,
                    admin.copy(password = hashedPassword)
                )

                if (updated) {
                    ResetCodeStorage.codes.remove(identifier)
                    call.respond(HttpStatusCode.OK, "Password berhasil diubah")
                } else {
                    call.respond(HttpStatusCode.InternalServerError, "Gagal mengubah password")
                }
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

// ✅ Token generator
fun generateToken(admin: Admin): String {
    return JWT.create()
        .withAudience("eventify-users")
        .withIssuer("eventify")
        .withClaim("whatsappNumber", admin.whatsappNumber)
        .withClaim("email", admin.email)
        .sign(Algorithm.HMAC256("secret")) // Gunakan environment variable untuk security
}

// ✅ Tempat simpan kode verifikasi (sementara)
object ResetCodeStorage {
    val codes = ConcurrentHashMap<String, String>()
}

// ✅ Data classes
@Serializable
data class AdminRequest(
    val whatsappNumber: String,
    val name: String,
    val email: String,
    val password: String
)

@Serializable
data class LoginRequest(
    val identifier: String, // bisa whatsapp number atau email
    val password: String
)
