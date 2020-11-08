package com.todo.service

import com.todo.model.NewTodo
import com.todo.model.Todo
import com.todo.model.Todos
import org.jetbrains.exposed.sql.*
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

    fun getAllTodo(): MutableList<Todo> {
        val todos: MutableList<Todo> = mutableListOf()
        transaction {
            Todos.selectAll().forEach { todos.add(toTodo(it)) }
        }
        return todos
    }

    fun addTodo(todo: NewTodo) {
        val formatter = DateTimeFormat.forPattern("yyyy-MM-dd")
        transaction {
            Todos.insert {
                it[title] = todo.title
                it[detail] = todo.detail
                it[date] = DateTime.parse(todo.date, formatter)
            }
        }
    }

    fun updateTodo(id: Long, todo: NewTodo) {
        val formatter = DateTimeFormat.forPattern("yyyy-MM-dd")
        transaction {
            Todos.update({ Todos.id eq id }) {
                it[title] = todo.title
                it[detail] = todo.detail
                it[date] = DateTime.parse(todo.date, formatter)
            }
        }
    }

    fun deleteTodo(id: Long): Boolean {
        return transaction {
            Todos.deleteWhere { Todos.id eq id } > 0
        }
    }

    fun countTodo() = transaction {
        Todos.selectAll().count()
    }

    private fun toTodo(row: ResultRow) =
        Todo(
            id = row[Todos.id],
            title = row[Todos.title],
            detail = row[Todos.detail],
            date = row[Todos.date].toString()
        )
}

