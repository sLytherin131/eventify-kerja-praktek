object AdminService {
    fun create(admin: Admin): Int = transaction {
        Admins.insert {
            it[name] = admin.name
            it[email] = admin.email
            it[whatsapp] = admin.whatsapp
            it[password] = admin.password
            it[createdAt] = org.joda.time.DateTime.now()
        } get Admins.id
    }

    fun login(emailOrPhone: String, password: String): Admin? = transaction {
        Admins.select {
            ((Admins.email eq emailOrPhone) or (Admins.whatsapp eq emailOrPhone)) and
                    (Admins.password eq password)
        }.map {
            Admin(it[Admins.id], it[Admins.name], it[Admins.email], it[Admins.whatsapp], it[Admins.password])
        }.singleOrNull()
    }
}
