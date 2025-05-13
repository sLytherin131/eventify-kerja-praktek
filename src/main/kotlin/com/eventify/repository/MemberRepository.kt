package com.eventify.repository

import com.eventify.models.Member
import com.eventify.models.Members
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class MemberRepository {

    fun create(member: Member): Member? {
        return try {
            transaction {
                Members.insert {
                    it[whatsappNumber] = member.whatsappNumber
                    it[name] = member.name
                    it[createdAt] = member.createdAt
                }
            }
            member
        } catch (e: Exception) {
            null
        }
    }

    fun getByWhatsappNumber(whatsappNumber: String): Member? {
        return transaction {
            Members.select { Members.whatsappNumber eq whatsappNumber }
                .map { toMember(it) }
                .singleOrNull()
        }
    }

    fun getAll(): List<Member> {
        return transaction {
            Members.selectAll()
                .map { toMember(it) }
        }
    }

    fun update(member: Member): Boolean {
        return try {
            transaction {
                val updatedRows = Members.update({ Members.whatsappNumber eq member.whatsappNumber }) {
                    it[name] = member.name
                    // createdAt usually not updated
                }
                updatedRows > 0
            }
        } catch (e: Exception) {
            false
        }
    }

    fun delete(whatsappNumber: String): Boolean {
        return try {
            transaction {
                val deletedRows = Members.deleteWhere { Members.whatsappNumber eq whatsappNumber }
                deletedRows > 0
            }
        } catch (e: Exception) {
            false
        }
    }

    private fun toMember(row: ResultRow) = Member(
        whatsappNumber = row[Members.whatsappNumber],
        name = row[Members.name],
        createdAt = row[Members.createdAt]
    )
}
