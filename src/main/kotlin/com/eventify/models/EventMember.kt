package com.eventify.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

object EventMembers : Table("event_members") {
    val id = integer("id").autoIncrement()
    val eventId = integer("event_id").references(Events.id)
    val memberWhatsapp = varchar("member_whatsapp", 50) // reference to members.whatsapp_number
    override val primaryKey = PrimaryKey(id, name = "PK_EventMembers_Id")
}

@Serializable
data class EventMember(
    val id: Int = 0,
    val eventId: Int,
    val memberWhatsapp: String
)