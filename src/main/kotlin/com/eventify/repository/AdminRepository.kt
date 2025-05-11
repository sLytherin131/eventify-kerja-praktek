package com.eventify.repository

import com.eventify.models.Admin
import com.eventify.models.Admins
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt

class AdminRepository {

    fun create(admin: Admin): Boolean = transaction {
        val hashedPassword = BCrypt.hashpw(admin.password, BCrypt.gensalt())
        val insertResult = Admins.insert {
            it[whatsappNumber] = admin.whatsappNumber
            it[name] = admin.name
            it[email] = admin.email
            it[password] = hashedPassword
            it[createdAt] = admin.createdAt
        }
        insertResult.insertedCount > 0
    }

    fun findByWhatsappNumber(whatsappNumber: String): Admin? = transaction {
        Admins.select { Admins.whatsappNumber eq whatsappNumber }
            .map {
                Admin(
                    whatsappNumber = it[Admins.whatsappNumber],
                    name = it[Admins.name],
                    email = it[Admins.email],
                    password = it[Admins.password],
                    createdAt = it[Admins.createdAt]
                )
            }.singleOrNull()
    }

    fun verifyPassword(plainPassword: String, hashedPassword: String): Boolean {
        return BCrypt.checkpw(plainPassword, hashedPassword)
    }
}
