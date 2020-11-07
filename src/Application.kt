package com.todo

import com.todo.service.DatabaseFactory
import com.todo.service.TodoService
import com.todo.web.todo
import io.ktor.application.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun Application.module() {
    DatabaseFactory.init()

    val todoService = TodoService()

    install(Routing) {
        todo(todoService)
    }
}

fun main(args: Array<String>) {
    embeddedServer(Netty, commandLineEnvironment(args)).start(wait = true)
}
