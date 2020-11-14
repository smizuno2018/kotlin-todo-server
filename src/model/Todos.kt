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

// https://qiita.com/ques0942/items/c4a20a21c9e1f0963591#%E3%82%AD%E3%83%A3%E3%83%A1%E3%83%AB%E3%82%B1%E3%83%BC%E3%82%B9%E3%81%AE%E3%83%95%E3%82%A3%E3%83%BC%E3%83%AB%E3%83%89%E3%82%92%E3%82%B9%E3%83%8D%E3%83%BC%E3%82%AF%E3%82%B1%E3%83%BC%E3%82%B9%E3%81%A7json%E3%81%AB%E5%87%BA%E5%8A%9B%E3%81%99%E3%82%8B
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
