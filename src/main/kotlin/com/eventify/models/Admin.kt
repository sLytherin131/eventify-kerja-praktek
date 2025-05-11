package com.eventify.models

import org.jetbrains.exposed.sql.Table
import java.time.Instant

object Admins : Table("admins") {
    val whatsappNumber = varchar("whatsapp_number", 50)
    val name = varchar("name", 255)
    val email = varchar("email", 255)
    val password = varchar("password", 255)
    val createdAt = long("created_at")

    override val primaryKey = PrimaryKey(whatsappNumber, name = "PK_Admins_WhatsappNumber")
}

@kotlinx.serialization.Serializable
data class Admin(
    val whatsappNumber: String,
    val name: String,
    val email: String,
    val password: String,
    val createdAt: Long = Instant.now().toEpochMilli()
)
