package com.eventify.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import com.eventify.models.Admins
import com.eventify.models.PersonalTasks
import com.eventify.models.Members
import com.eventify.models.Events
import com.eventify.models.EventMembers
import com.eventify.models.EventTasks

object DatabaseFactory {
    fun init() {
        val config = HikariConfig().apply {
            jdbcUrl = "jdbc:mysql://eventifydb.railway.internal:3306/railway"
            driverClassName = "com.mysql.cj.jdbc.Driver"
            username = "root"
            password = "lUWqeEfwfLGkzfnxKtacCENwMXnuqJOU"
            maximumPoolSize = 3
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        }
        val dataSource = HikariDataSource(config)
        Database.connect(dataSource)

        transaction {
            SchemaUtils.createMissingTablesAndColumns(Admins, PersonalTasks, Members, EventMembers, EventTasks)
        }
    }
}
