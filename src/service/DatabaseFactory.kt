package com.todo.service

import com.todo.model.Todos
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {

    fun init() {
        Database.connect(
            "jdbc:postgresql://localhost:5433/todo",
            "org.postgresql.Driver",
            "admin",
            "password"
        )

        transaction {
            create(Todos)
        }
    }
}
