package com.todo.web

import com.fasterxml.jackson.core.JsonParseException
import com.todo.model.*
import com.todo.model.ErrorCode.Companion.DELETE_ERROR_CODE
import com.todo.model.ErrorCode.Companion.GET_ERROR_CODE
import com.todo.model.ErrorCode.Companion.POST_ERROR_CODE
import com.todo.model.ErrorCode.Companion.PUT_ERROR_CODE
import com.todo.model.ErrorMessage.Companion.DELETE_ERROR_MESSAGE
import com.todo.model.ErrorMessage.Companion.GET_ERROR_MESSAGE
import com.todo.model.ErrorMessage.Companion.POST_ERROR_MESSAGE
import com.todo.model.ErrorMessage.Companion.PUT_ERROR_MESSAGE
import com.todo.service.TodoService
import io.ktor.application.*
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.todo(todoService: TodoService) {
    route("/todos") {
        get {
            try {
                val todos = todoService.getAllTodo()
                call.respond(OK, TodosResponse(todos))
            } catch (e: RecordInvalidException) {
                throw  InternalServerErrorException(GET_ERROR_MESSAGE, GET_ERROR_CODE)
            }
        }

        post {
            try {
                val newTodo = call.receive<NewTodo>()
                todoService.addTodo(newTodo)
                call.respond(OK, TodoResponse())
            } catch (e: Exception) {
                when (e) {
                    is JsonParseException -> {
                        throw BadRequestException()
                    }
                    is RecordInvalidException -> {
                        throw  InternalServerErrorException(POST_ERROR_MESSAGE, POST_ERROR_CODE)
                    }
                    else -> throw e
                }
            }
        }

        put("/{id}") {
            try {
                val id = call.parameters["id"]!!.toLong()
                val newTodo = call.receive<NewTodo>()
                todoService.updateTodo(id, newTodo)
                call.respond(OK, TodoResponse())
            } catch (e: Exception) {
                when (e) {
                    is NullPointerException, is JsonParseException -> {
                        throw BadRequestException()
                    }
                    is RecordInvalidException -> {
                        throw  InternalServerErrorException(PUT_ERROR_MESSAGE, PUT_ERROR_CODE)
                    }
                    else -> throw e
                }
            }
        }

        delete("/{id}") {
            try {
                val id = call.parameters["id"]!!.toLong()
                todoService.deleteTodo(id)
                call.respond(OK, TodoResponse())
            } catch (e: Exception) {
                when (e) {
                    is NullPointerException -> {
                        throw BadRequestException()
                    }
                    is RecordInvalidException -> {
                        throw  InternalServerErrorException(DELETE_ERROR_MESSAGE, DELETE_ERROR_CODE)
                    }
                    else -> throw e
                }
            }
        }
    }
}
