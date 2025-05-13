package com.eventify.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table
import java.time.Instant

object PersonalTasks : Table("personal_tasks") {
    val id = integer("id").autoIncrement() // Adjust according to your schema if autoIncrement is needed
    val adminWhatsapp = varchar("admin_whatsapp", 50).references(Admins.whatsappNumber)
    val description = text("description")
    val taskType = varchar("task_type", 50) // note: 'personal, work, or urgent'
    val createdAt = long("created_at")

    override val primaryKey = PrimaryKey(id, name = "PK_PersonalTasks_Id")
}

@Serializable
data class PersonalTask(
    val id: Int = 0,
    val adminWhatsapp: String,
    val description: String,
    val taskType: String,
    val createdAt: Long = Instant.now().toEpochMilli()
)