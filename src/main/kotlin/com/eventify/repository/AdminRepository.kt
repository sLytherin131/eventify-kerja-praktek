package com.eventify.repository

import com.eventify.models.Admin
import com.eventify.models.Admins
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import at.favre.lib.crypto.bcrypt.BCrypt

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

    fun getAll(): List<Admin> = transaction {
        Admins.selectAll().map { toAdmin(it) }
    }

    fun create(admin: Admin): Boolean = transaction {
        val insertResult = Admins.insert {
            it[whatsappNumber] = admin.whatsappNumber
            it[name] = admin.name
            it[email] = admin.email
            it[password] = admin.password
            it[createdAt] = admin.createdAt
        }
        insertResult.insertedCount > 0
    }

    fun updateAdmin(whatsappNumber: String, updatedAdmin: Admin): Boolean = transaction {
        val updatedRows = Admins.update({ Admins.whatsappNumber eq whatsappNumber }) {
            it[name] = updatedAdmin.name
            it[email] = updatedAdmin.email
            it[password] = updatedAdmin.password
            it[createdAt] = updatedAdmin.createdAt
        }
        updatedRows > 0
    }

    fun deleteAdmin(whatsappNumber: String): Boolean = transaction {
        val deletedRows = Admins.deleteWhere { Admins.whatsappNumber eq whatsappNumber }
        deletedRows > 0
    }

    // ✅ Hash password dengan bcrypt
    fun hashPassword(plainPassword: String): String {
        return BCrypt.withDefaults().hashToString(12, plainPassword.toCharArray())
    }

    // ✅ Verifikasi password dengan bcrypt
    fun verifyPassword(inputPassword: String, storedHashedPassword: String): Boolean {
        val result = BCrypt.verifyer().verify(inputPassword.toCharArray(), storedHashedPassword)
        return result.verified
    }

    private fun toAdmin(row: ResultRow): Admin = Admin(
        whatsappNumber = row[Admins.whatsappNumber],
        name = row[Admins.name],
        email = row[Admins.email],
        password = row[Admins.password],
        createdAt = row[Admins.createdAt]
    )
}
