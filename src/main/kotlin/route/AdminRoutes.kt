package com.example.route

import com.example.service.AdminService
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.adminRoutes() {
    route("/admin") {
        post("/create") {
            val admin = call.receive<AdminService.AdminDTO>()
            val created = AdminService.createAdmin(admin)
            if (created) {
                call.respondText("Admin created successfully")
            } else {
                call.respondText("Admin already exists")
            }
        }

        post("/login") {
            val credentials = call.receive<Map<String, String>>()
            val success = AdminService.login(
                credentials["whatsappNumber"] ?: "",
                credentials["password"] ?: ""
            )
            if (success) {
                call.respondText("Login successful")
            } else {
                call.respondText("Invalid credentials")
            }
        }
    }
}
