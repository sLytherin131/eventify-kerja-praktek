package com.example.model

import org.jetbrains.exposed.sql.Table
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
 // pastikan kamu pakai yang sesuai

object Admins : Table("admins") {
    val whatsappNumber = varchar("whatsapp_number", 20)
    val name = varchar("name", 100)
    val email = varchar("email", 100)
    val password = varchar("password", 100)
    val createdAt = datetime("created_at")

    private fun datetime(s: String) {

    }

    override val primaryKey = PrimaryKey(whatsappNumber)
}
