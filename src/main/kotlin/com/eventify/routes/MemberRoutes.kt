package com.eventify.routes

import com.eventify.models.Member
import com.eventify.repository.MemberRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

fun Application.registerMemberRoutes() {
    val repository = MemberRepository()

    routing {
        route("/members") {
            get {
                val members = repository.getAll()
                call.respond(HttpStatusCode.OK, members)
            }

            get("{whatsappNumber}") {
                val whatsappNumber = call.parameters["whatsappNumber"]
                if (whatsappNumber.isNullOrEmpty()) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid WhatsApp number")
                    return@get
                }
                val member = repository.getByWhatsappNumber(whatsappNumber)
                if (member == null) {
                    call.respond(HttpStatusCode.NotFound, "Member not found")
                } else {
                    call.respond(HttpStatusCode.OK, member)
                }
            }

            post {
                val request = call.receive<MemberRequest>()
                val member = Member(
                    whatsappNumber = request.whatsappNumber,
                    name = request.name
                )
                val createdMember = repository.create(member)
                if (createdMember != null) {
                    call.respond(HttpStatusCode.Created, createdMember)
                } else {
                    call.respond(HttpStatusCode.InternalServerError, "Failed to create member")
                }
            }

            put("{whatsappNumber}") {
                val whatsappNumber = call.parameters["whatsappNumber"]
                if (whatsappNumber.isNullOrEmpty()) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid WhatsApp number")
                    return@put
                }
                val request = call.receive<UpdateMemberRequest>()
                val existingMember = repository.getByWhatsappNumber(whatsappNumber)
                if (existingMember == null) {
                    call.respond(HttpStatusCode.NotFound, "Member not found")
                    return@put
                }
                val updatedMember = existingMember.copy(
                    name = request.name ?: existingMember.name
                )
                if (repository.update(updatedMember)) {
                    call.respond(HttpStatusCode.OK, updatedMember)
                } else {
                    call.respond(HttpStatusCode.InternalServerError, "Failed to update member")
                }
            }

            delete("{whatsappNumber}") {
                val whatsappNumber = call.parameters["whatsappNumber"]
                if (whatsappNumber.isNullOrEmpty()) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid WhatsApp number")
                    return@delete
                }
                if (repository.delete(whatsappNumber)) {
                    call.respond(HttpStatusCode.OK, "Member deleted successfully")
                } else {
                    call.respond(HttpStatusCode.InternalServerError, "Failed to delete member")
                }
            }
        }
    }
}

@Serializable
data class MemberRequest(
    val whatsappNumber: String,
    val name: String
)

@Serializable
data class UpdateMemberRequest(
    val name: String? = null
)
