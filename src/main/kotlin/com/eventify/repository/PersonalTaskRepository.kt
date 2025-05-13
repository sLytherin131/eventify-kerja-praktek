package com.eventify.repository

import com.eventify.models.PersonalTask
import com.eventify.models.PersonalTasks
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class PersonalTaskRepository {

    fun create(task: PersonalTask): PersonalTask? {
        return try {
            val generatedId = transaction {
                PersonalTasks.insert {
                    it[adminWhatsapp] = task.adminWhatsapp
                    it[description] = task.description
                    it[taskType] = task.taskType
                    it[createdAt] = task.createdAt
                } get PersonalTasks.id
            }
            task.copy(id = generatedId ?: 0)
        } catch (e: Exception) {
            // Log error e.g., println("Error creating personal task: ${e.localizedMessage}")
            null
        }
    }

    fun getById(id: Int): PersonalTask? {
        return transaction {
            PersonalTasks.select { PersonalTasks.id eq id }
                .map { toPersonalTask(it) }
                .singleOrNull()
        }
    }

    fun getAllByAdmin(adminWhatsapp: String): List<PersonalTask> {
        return transaction {
            PersonalTasks.select { PersonalTasks.adminWhatsapp eq adminWhatsapp }
                .map { toPersonalTask(it) }
        }
    }

    fun update(task: PersonalTask): Boolean {
        return try {
            transaction {
                val updatedRows = PersonalTasks.update({ PersonalTasks.id eq task.id }) {
                    it[description] = task.description
                    it[taskType] = task.taskType
                    // Optionally update createdAt if needed
                }
                updatedRows > 0
            }
        } catch (e: Exception) {
            false
        }
    }

    fun delete(id: Int): Boolean {
        return try {
            transaction {
                val deletedRows = PersonalTasks.deleteWhere { PersonalTasks.id eq id }
                deletedRows > 0
            }
        } catch (e: Exception) {
            false
        }
    }

    private fun toPersonalTask(row: ResultRow) = PersonalTask(
        id = row[PersonalTasks.id],
        adminWhatsapp = row[PersonalTasks.adminWhatsapp],
        description = row[PersonalTasks.description],
        taskType = row[PersonalTasks.taskType],
        createdAt = row[PersonalTasks.createdAt]
    )
}
