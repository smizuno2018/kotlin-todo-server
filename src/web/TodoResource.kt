package com.todo.web

import com.todo.service.TodoService
import io.ktor.application.*
import io.ktor.request.*
import io.ktor.routing.*

fun Route.todo(todoService: TodoService) {
    route("/todos") {
        post {
            todoService.addTodo(call.receive())
        }
    }

    put("/{id}") {
        val id = call.parameters["id"]!!.toLong()
        todoService.updateTodo(id, call.receive())
    }
}
