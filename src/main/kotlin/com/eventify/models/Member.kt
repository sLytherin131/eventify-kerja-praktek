package com.eventify.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table
import java.time.Instant

object Members : Table("members") {
    val whatsappNumber = varchar("whatsapp_number", 50)
    val name = varchar("name", 255)
    val createdAt = long("created_at")

    override val primaryKey = PrimaryKey(whatsappNumber, name = "PK_Members_WhatsappNumber")
}

@Serializable
data class Member(
    val whatsappNumber: String,
    val name: String,
    val createdAt: Long = Instant.now().toEpochMilli()
)
