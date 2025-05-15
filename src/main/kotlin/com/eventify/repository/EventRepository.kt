package com.eventify.repository

import com.eventify.models.*
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction

class EventRepository {

    fun createCompositeEvent(event: Event, tasks: List<EventTask>, members: List<EventMember>): Event? {
        return try {
            transaction {
                // Insert event and get generated id
                val eventId = Events.insert {
                    it[name] = event.name
                    it[description] = event.description
                    it[startTime] = event.startTime
                    it[endTime] = event.endTime
                    it[createdBy] = event.createdBy
                } get Events.id

                // Insert event tasks using eventId
                tasks.forEach { task ->
                    EventTasks.insert {
                        it[EventTasks.eventId] = eventId
                        it[description] = task.description
                        it[taskType] = task.taskType
                        it[createdAt] = task.createdAt
                    }
                }

                // Insert event members using eventId
                members.forEach { member ->
                    EventMembers.insert {
                        it[EventMembers.eventId] = eventId
                        it[memberWhatsapp] = member.memberWhatsapp
                    }
                }
                // Return the event with generated id
                event.copy(id = eventId)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}