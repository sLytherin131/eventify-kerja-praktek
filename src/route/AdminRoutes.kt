fun Route.adminRoutes() {
    post("/create_new_admin") {
        val admin = call.receive<Admin>()
        val id = AdminService.create(admin)
        call.respond(HttpStatusCode.Created, mapOf("id" to id))
    }

    post("/login") {
        val credentials = call.receive<Map<String, String>>()
        val emailOrPhone = credentials["emailOrPhone"] ?: ""
        val password = credentials["password"] ?: ""
        val admin = AdminService.login(emailOrPhone, password)
        if (admin != null) call.respond(admin)
        else call.respond(HttpStatusCode.Unauthorized, "Invalid credentials")
    }
}
