package com.eventify.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table
import java.time.Instant

object Events : Table("events") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 255)
    val description = text("description")
    val startTime = long("start_time")
    val endTime = long("end_time")
    val createdBy = varchar("created_by", 50)
    override val primaryKey = PrimaryKey(id, name = "PK_Events_Id")
}

@Serializable
data class Event(
    val id: Int = 0,
    val name: String,
    val description: String,
    val startTime: Long,
    val endTime: Long,
    val createdBy: String
)