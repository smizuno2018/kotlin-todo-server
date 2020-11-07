package com.todo.service

import com.todo.model.NewTodo
import com.todo.model.Todo
import com.todo.model.Todos
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

class TodoService {
    fun getTodo(id: Long) =
        transaction {
            Todos.select {
                (Todos.id eq id)
            }.mapNotNull {
                toTodo(it)
            }.singleOrNull()
        }

    fun addTodo(todo: NewTodo): Todo {
        val formatter = DateTimeFormat.forPattern("yyyy-MM-dd")
        var key: Long = 0
        transaction {
            key = ((Todos.insert {
                it[title] = todo.title
                it[detail] = todo.detail
                it[date] = DateTime.parse(todo.date, formatter)
            } get Todos.id))
        }
        return getTodo(key)!!
    }

    private fun toTodo(row: ResultRow) =
        Todo(
            id = row[Todos.id],
            title = row[Todos.title],
            detail = row[Todos.detail],
            date = row[Todos.date].toString()
        )
}
