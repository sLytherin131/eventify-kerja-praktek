package com.eventify.repository

import com.eventify.models.Admin
import com.eventify.models.Admins
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

class AdminRepository {

    fun findByWhatsappNumber(whatsappNumber: String): Admin? = transaction {
        Admins.select { Admins.whatsappNumber eq whatsappNumber }
            .map { toAdmin(it) }
            .singleOrNull()
    }

    fun findByEmail(email: String): Admin? = transaction {
        Admins.select { Admins.email eq email }
            .map { toAdmin(it) }
            .singleOrNull()
    }

    private fun toAdmin(row: org.jetbrains.exposed.sql.ResultRow): Admin = Admin(
        whatsappNumber = row[Admins.whatsappNumber],
        name = row[Admins.name],
        email = row[Admins.email],
        password = row[Admins.password],
        createdAt = row[Admins.createdAt]
    )

    fun verifyPassword(inputPassword: String, storedPassword: String): Boolean {
        // Implement your password verification logic here, e.g., hashing comparison
        return inputPassword == storedPassword
    }

    fun create(admin: Admin): Boolean = transaction {
        val insertResult = Admins.insert {
            it[Admins.whatsappNumber] = admin.whatsappNumber
            it[Admins.name] = admin.name
            it[Admins.email] = admin.email
            it[Admins.password] = admin.password
            it[Admins.createdAt] = admin.createdAt
        }
        insertResult.insertedCount > 0
    }
}