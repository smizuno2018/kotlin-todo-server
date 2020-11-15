package com.todo.service

import com.todo.model.NewTodo
import com.todo.model.RecordInvalidException
import com.todo.model.Todo
import com.todo.model.Todos
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

class TodoService {

    fun getAllTodo(): MutableList<Todo> {
        val todos: MutableList<Todo> = mutableListOf()
        try {
            transaction {
                Todos.selectAll().forEach { todos.add(makeTodo(it)) }
            }
        } catch (e: Throwable) {
            when (e) {
                is ExposedSQLException -> throw RecordInvalidException()
                else -> throw e
            }
        }
        return todos
    }

    fun addTodo(todo: NewTodo) {
        val formatter = DateTimeFormat.forPattern("yyyy-MM-dd")
        try {
            transaction {
                Todos.insert {
                    it[title] = todo.title
                    it[detail] = todo.detail
                    it[date] = DateTime.parse(todo.date, formatter)
                }
            }
        } catch (e: Throwable) {
            when (e) {
                is IllegalArgumentException, is ExposedSQLException -> throw RecordInvalidException()
                else -> throw e
            }
        }
    }

    fun updateTodo(id: Long, todo: NewTodo) {
        val formatter = DateTimeFormat.forPattern("yyyy-MM-dd")
        try {
            transaction {
                val isFailed = Todos.update({ Todos.id eq id }) {
                    it[title] = todo.title
                    it[detail] = todo.detail
                    it[date] = DateTime.parse(todo.date, formatter)
                } == 0
                if (isFailed) throw RecordInvalidException()
            }
        } catch (e: Throwable) {
            when (e) {
                is IllegalArgumentException, is RecordInvalidException, is ExposedSQLException -> {
                    throw RecordInvalidException()
                }
                else -> throw e
            }
        }
    }

    fun deleteTodo(id: Long) {
        try {
            transaction {
                val isFailed = Todos.deleteWhere { Todos.id eq id } == 0
                if (isFailed) throw RecordInvalidException()
            }
        } catch (e: Throwable) {
            when (e) {
                is RecordInvalidException, is ExposedSQLException -> throw RecordInvalidException()
                else -> throw e
            }
        }
    }

    //region Only Test
    fun getTodo(id: Long) = transaction {
        Todos.select {
            (Todos.id eq id)
        }.mapNotNull {
            makeTodo(it)
        }.singleOrNull()
    }

    fun countTodo() = transaction {
        Todos.selectAll().count()
    }
    //endregion

    private fun makeTodo(row: ResultRow) =
        Todo(
            id = row[Todos.id],
            title = row[Todos.title],
            detail = row[Todos.detail],
            date = row[Todos.date].toString()
        )
}

