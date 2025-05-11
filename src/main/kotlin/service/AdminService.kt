package com.example.service

import com.example.model.Admins
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq


data class AdminDTO(val whatsappNumber: String, val name: String, val email: String, val password: String)

object AdminService {
    fun createAdmin(admin: AdminDTO): Boolean {
        return transaction {
            if (Admins.select { Admins.whatsappNumber eq admin.whatsappNumber }.count() > 0) return@transaction false
            Admins.insert {
                it[whatsappNumber] = admin.whatsappNumber
                it[name] = admin.name
                it[email] = admin.email
                it[password] = admin.password
                it[createdAt] = LocalDateTime.now()
            }
            true
        }
    }

    fun login(whatsappNumber: String, password: String): Boolean {
        return transaction {
            Admins.select { (Admins.whatsappNumber eq whatsappNumber) and (Admins.password eq password) }
                .count() > 0
        }
    }
}
