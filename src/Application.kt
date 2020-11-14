package com.todo

import com.fasterxml.jackson.databind.SerializationFeature
import com.todo.model.SystemException
import com.todo.service.DatabaseFactory
import com.todo.service.TodoService
import com.todo.web.todo
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.jackson.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun Application.module() {
    DatabaseFactory.init()

    install(ContentNegotiation) {
        jackson {
            configure(SerializationFeature.INDENT_OUTPUT, true)
        }
    }
    
    install(StatusPages) {
        exception<SystemException> { cause ->
            call.response.status(cause.status)
            call.respond(cause.response())
        }
    }

    val todoService = TodoService()

    install(Routing) {
        todo(todoService)
    }
}

fun main(args: Array<String>) {
    embeddedServer(Netty, commandLineEnvironment(args)).start(wait = true)
}
