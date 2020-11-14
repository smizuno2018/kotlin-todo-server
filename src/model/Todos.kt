package com.todo.model

import com.fasterxml.jackson.annotation.JsonProperty
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.jodatime.CurrentDateTime
import org.jetbrains.exposed.sql.jodatime.date
import org.jetbrains.exposed.sql.jodatime.datetime

object Todos : Table() {
    val id = long("id").autoIncrement().check { it greaterEq TodosConstant.ID_START }
    val title = varchar("title", TodosConstant.TITLE_MAX_LENGTH)
    val detail = varchar("detail", TodosConstant.DETAIL_MAX_LENGTH).nullable()
    val date = date("date").nullable()
    private val createdAt = datetime("created_at").defaultExpression(CurrentDateTime())
    private val updatedAt = datetime("updated_at").defaultExpression(CurrentDateTime())

    override val primaryKey = PrimaryKey(id)
}

data class Todo(
    val id: Long,
    val title: String,
    val detail: String?,
    val date: String?
)

data class NewTodo(
    val title: String,
    val detail: String?,
    val date: String?
)

data class TodoResponse(
    @JsonProperty("error_code")
    override val errorCode: Int = 0,
    @JsonProperty("error_message")
    override val errorMessage: String = ""
) : BaseResponse

data class TodosResponse(
    val todos: List<Todo>,
    @JsonProperty("error_code")
    override val errorCode: Int = 0,
    @JsonProperty("error_message")
    override val errorMessage: String = ""
) : BaseResponse

class TodosConstant {
    companion object {
        const val TITLE_MAX_LENGTH = 100
        const val DETAIL_MAX_LENGTH = 1000
        const val ID_START = 1L
    }
}
