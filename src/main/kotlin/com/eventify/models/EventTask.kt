package com.eventify.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table
import java.time.Instant

object EventTasks : Table("event_tasks") {
    val id = integer("id").autoIncrement()
    val eventId = integer("event_id").references(Events.id)
    val description = text("description")
    val taskType = varchar("task_type", 50)
    val createdAt = long("created_at")
    override val primaryKey = PrimaryKey(id, name = "PK_EventTasks_Id")
}

@Serializable
data class EventTask(
    val id: Int = 0,
    val eventId: Int,
    val description: String,
    val taskType: String,
    val createdAt: Long = Instant.now().toEpochMilli()
)