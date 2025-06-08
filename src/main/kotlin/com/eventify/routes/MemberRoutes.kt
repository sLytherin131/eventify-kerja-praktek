package com.eventify.routes

import com.eventify.models.Member
import com.eventify.repository.MemberRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.registerMemberRoutes() {
    val memberRepository = MemberRepository()

    routing {
        authenticate("auth-jwt") {
            route("/members") {
                // Create member
                post {
                    val memberRequest = call.receive<MemberRequest>()
                    val member = Member(
                        whatsappNumber = memberRequest.whatsappNumber,
                        name = memberRequest.name,
                        createdAt = System.currentTimeMillis()
                    )
                    val created = memberRepository.create(member)
                    if (created != null) {
                        call.respond(HttpStatusCode.Created, created)
                    } else {
                        call.respond(HttpStatusCode.InternalServerError, "Failed to create member")
                    }
                }

                // Get all members
                get {
                    val members = memberRepository.getAll()
                    call.respond(members)
                }

                // Get member by WhatsApp number
                get("/{whatsappNumber}") {
                    val whatsappNumber = call.parameters["whatsappNumber"]
                    if (whatsappNumber == null) {
                        call.respond(HttpStatusCode.BadRequest, "WhatsApp number is required")
                        return@get
                    }
                    val member = memberRepository.getByWhatsappNumber(whatsappNumber)
                    if (member != null) {
                        call.respond(member)
                    } else {
                        call.respond(HttpStatusCode.NotFound, "Member not found")
                    }
                }

                // Update member by WhatsApp number
                put("/{whatsappNumber}") {
                    val whatsappNumber = call.parameters["whatsappNumber"]
                    if (whatsappNumber == null) {
                        call.respond(HttpStatusCode.BadRequest, "WhatsApp number is required")
                        return@put
                    }

                    val memberRequest = call.receive<MemberRequest>()
                    val updated = memberRepository.update(
                        Member(
                            whatsappNumber = whatsappNumber,
                            name = memberRequest.name,
                            createdAt = System.currentTimeMillis() // optional: not updated
                        )
                    )
                    if (updated) {
                        call.respond(HttpStatusCode.OK, "Member updated successfully")
                    } else {
                        call.respond(HttpStatusCode.NotFound, "Member not found or update failed")
                    }
                }

                // Delete member by WhatsApp number
                delete("/{whatsappNumber}") {
                    val whatsappNumber = call.parameters["whatsappNumber"]
                    if (whatsappNumber == null) {
                        call.respond(HttpStatusCode.BadRequest, "WhatsApp number is required")
                        return@delete
                    }

                    val deleted = memberRepository.delete(whatsappNumber)
                    if (deleted) {
                        call.respond(HttpStatusCode.OK, "Member deleted successfully")
                    } else {
                        call.respond(HttpStatusCode.NotFound, "Member not found or deletion failed")
                    }
                }
            }
        }
    }
}

@kotlinx.serialization.Serializable
data class MemberRequest(
    val whatsappNumber: String,
    val name: String
)
