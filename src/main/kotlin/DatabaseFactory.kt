package com.example

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import com.example.model.Admins
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger

object DatabaseFactory {
    fun init() {
        Database.connect(
            url = "jdbc:mysql://localhost:3306/namadbkamu",
            driver = "com.mysql.cj.jdbc.Driver",
            user = "root",
            password = "passwordkamu"
        )

        transaction {
            addLogger(StdOutSqlLogger)
            SchemaUtils.create(Admins) // hanya tabel yang ingin kamu buat jika belum ada
        }
    }
}
