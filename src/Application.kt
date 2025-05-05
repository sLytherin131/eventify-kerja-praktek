fun Application.module() {
    install(ContentNegotiation) { gson() }

    DatabaseFactory.init()

    routing {
        adminRoutes()
        // Tambahkan eventRoutes(), taskRoutes(), dsb.
    }
}
