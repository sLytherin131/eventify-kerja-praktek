package com.eventify.repository

import com.eventify.models.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import kotlinx.serialization.Serializable
import java.time.Instant
import java.time.temporal.ChronoUnit

@Serializable
data class EventWithDetails(
    val event: Event,
    val tasks: List<EventTask>,
    val members: List<EventMember>
)

class EventRepository {

    fun createCompositeEvent(event: Event, tasks: List<EventTask>, members: List<EventMember>): Event? {
        return try {
            transaction {
                val eventId = Events.insert {
                    it[name] = event.name
                    it[description] = event.description
                    it[startTime] = event.startTime
                    it[endTime] = event.endTime
                    it[createdBy] = event.createdBy
                    it[reminderSent] = false
                } get Events.id

                tasks.forEach { task ->
                    EventTasks.insert {
                        it[EventTasks.eventId] = eventId
                        it[description] = task.description
                        it[taskType] = task.taskType
                        it[createdAt] = task.createdAt
                    }
                }

                members.forEach { member ->
                    EventMembers.insert {
                        it[EventMembers.eventId] = eventId
                        it[memberWhatsapp] = member.memberWhatsapp
                    }
                }

                event.copy(id = eventId)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun getAllEventsWithDetails(): List<EventWithDetails> = transaction {
        Events.selectAll().map { eventRow ->
            val eventId = eventRow[Events.id]

            val tasks = EventTasks.select { EventTasks.eventId eq eventId }.map {
                EventTask(
                    id = it[EventTasks.id],
                    eventId = it[EventTasks.eventId],
                    description = it[EventTasks.description],
                    taskType = it[EventTasks.taskType],
                    createdAt = it[EventTasks.createdAt]
                )
            }

            val members = EventMembers.select { EventMembers.eventId eq eventId }.map {
                EventMember(
                    id = it[EventMembers.id],
                    eventId = it[EventMembers.eventId],
                    memberWhatsapp = it[EventMembers.memberWhatsapp]
                )
            }

            EventWithDetails(
                event = Event(
                    id = eventRow[Events.id],
                    name = eventRow[Events.name],
                    description = eventRow[Events.description],
                    startTime = eventRow[Events.startTime],
                    endTime = eventRow[Events.endTime],
                    createdBy = eventRow[Events.createdBy],
                    reminderSent = eventRow[Events.reminderSent]
                ),
                tasks = tasks,
                members = members
            )
        }
    }

    fun getEventsByCreatedBy(createdBy: String): List<EventWithDetails> = transaction {
        Events.select { Events.createdBy eq createdBy }.map { eventRow ->
            val eventId = eventRow[Events.id]

            val tasks = EventTasks.select { EventTasks.eventId eq eventId }.map {
                EventTask(
                    id = it[EventTasks.id],
                    eventId = it[EventTasks.eventId],
                    description = it[EventTasks.description],
                    taskType = it[EventTasks.taskType],
                    createdAt = it[EventTasks.createdAt]
                )
            }

            val members = EventMembers.select { EventMembers.eventId eq eventId }.map {
                EventMember(
                    id = it[EventMembers.id],
                    eventId = it[EventMembers.eventId],
                    memberWhatsapp = it[EventMembers.memberWhatsapp]
                )
            }

            EventWithDetails(
                event = Event(
                    id = eventRow[Events.id],
                    name = eventRow[Events.name],
                    description = eventRow[Events.description],
                    startTime = eventRow[Events.startTime],
                    endTime = eventRow[Events.endTime],
                    createdBy = eventRow[Events.createdBy],
                    reminderSent = eventRow[Events.reminderSent]
                ),
                tasks = tasks,
                members = members
            )
        }
    }

    fun getEventWithDetailsById(id: Int): EventWithDetails? = transaction {
        val eventRow = Events.select { Events.id eq id }.singleOrNull() ?: return@transaction null

        val tasks = EventTasks.select { EventTasks.eventId eq id }.map {
            EventTask(
                id = it[EventTasks.id],
                eventId = it[EventTasks.eventId],
                description = it[EventTasks.description],
                taskType = it[EventTasks.taskType],
                createdAt = it[EventTasks.createdAt]
            )
        }

        val members = EventMembers.select { EventMembers.eventId eq id }.map {
            EventMember(
                id = it[EventMembers.id],
                eventId = it[EventMembers.eventId],
                memberWhatsapp = it[EventMembers.memberWhatsapp]
            )
        }

        EventWithDetails(
            event = Event(
                id = eventRow[Events.id],
                name = eventRow[Events.name],
                description = eventRow[Events.description],
                startTime = eventRow[Events.startTime],
                endTime = eventRow[Events.endTime],
                createdBy = eventRow[Events.createdBy],
                reminderSent = eventRow[Events.reminderSent]
            ),
            tasks = tasks,
            members = members
        )
    }

    fun updateCompositeEvent(id: Int, newEvent: Event, tasks: List<EventTask>, members: List<EventMember>): Boolean = transaction {
        val updated = Events.update({ Events.id eq id }) {
            it[name] = newEvent.name
            it[description] = newEvent.description
            it[startTime] = newEvent.startTime
            it[endTime] = newEvent.endTime
            it[createdBy] = newEvent.createdBy
            it[reminderSent] = newEvent.reminderSent
        }

        EventTasks.deleteWhere { EventTasks.eventId eq id }
        tasks.forEach { task ->
            EventTasks.insert {
                it[eventId] = id
                it[description] = task.description
                it[taskType] = task.taskType
                it[createdAt] = task.createdAt
            }
        }

        EventMembers.deleteWhere { EventMembers.eventId eq id }
        members.forEach { member ->
            EventMembers.insert {
                it[eventId] = id
                it[memberWhatsapp] = member.memberWhatsapp
            }
        }

        updated > 0
    }

    fun deleteEventById(id: Int): Boolean = transaction {
        EventTasks.deleteWhere { EventTasks.eventId eq id }
        EventMembers.deleteWhere { EventMembers.eventId eq id }
        Events.deleteWhere { Events.id eq id } > 0
    }

    // 🔔 Tambahan: Ambil event yang H-3 dan belum dikirim reminder
    fun getEventsForHMinus3Reminder(): List<EventWithDetails> = transaction {
        val zoneId = ZoneId.of("Asia/Jakarta")
        val jakartaNow = Instant.now().atZone(zoneId)
        val h3Start = jakartaNow.plusDays(3).toLocalDate().atStartOfDay(zoneId).toInstant()
        val h3End = h3Start.plus(1, ChronoUnit.DAYS).minusMillis(1)

        Events.select {
            (Events.reminderSent eq false) and
            (Events.startTime.between(h3Start.toEpochMilli(), h3End.toEpochMilli()))
        }.map { eventRow ->
            val eventId = eventRow[Events.id]

            val tasks = EventTasks.select { EventTasks.eventId eq eventId }.map {
                EventTask(
                    id = it[EventTasks.id],
                    eventId = it[EventTasks.eventId],
                    description = it[EventTasks.description],
                    taskType = it[EventTasks.taskType],
                    createdAt = it[EventTasks.createdAt]
                )
            }

            val members = EventMembers.select { EventMembers.eventId eq eventId }.map {
                EventMember(
                    id = it[EventMembers.id],
                    eventId = it[EventMembers.eventId],
                    memberWhatsapp = it[EventMembers.memberWhatsapp]
                )
            }

            EventWithDetails(
                event = Event(
                    id = eventRow[Events.id],
                    name = eventRow[Events.name],
                    description = eventRow[Events.description],
                    startTime = eventRow[Events.startTime],
                    endTime = eventRow[Events.endTime],
                    createdBy = eventRow[Events.createdBy],
                    reminderSent = eventRow[Events.reminderSent]
                ),
                tasks = tasks,
                members = members
            )
        }
    }

    // ✅ Tandai bahwa reminder sudah dikirim
    fun markReminderSent(eventId: Int): Boolean = transaction {
        Events.update({ Events.id eq eventId }) {
            it[reminderSent] = true
        } > 0
    }
}
