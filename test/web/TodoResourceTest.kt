package web

import com.todo.model.NewTodo
import com.todo.model.TodoResponse
import com.todo.model.TodosConstant.Companion.ID_START
import com.todo.model.TodosResponse
import common.ServerTest
import io.restassured.RestAssured.*
import io.restassured.http.ContentType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class TodoResourceTest : ServerTest() {

    @Test
    fun testCreateTodo() {
        // when
        val todo1 = NewTodo("title", "detail", "2020-01-01")
        val retrieved = post(todo1)

        // then
        assertThat(retrieved.errorCode).isEqualTo(0)
        assertThat(retrieved.errorMessage).isEqualTo("")
    }

    @Test
    fun testGetTodos() {
        // given
        val todo1 = NewTodo("title", "detail", "2020-01-01")
        val todo2 = NewTodo("title2", "detail2", "2020-02-02")
        post(todo1)
        post(todo2)

        // when
        val retrieved = get("/todos")
            .then()
            .statusCode(200)
            .extract().to<TodosResponse>()

        // then
        assertThat(retrieved.todos).hasSize(2)
        assertThat(retrieved.todos).extracting("title").containsExactlyInAnyOrder(todo1.title, todo2.title)
        assertThat(retrieved.todos).extracting("detail").containsExactlyInAnyOrder(todo1.detail, todo2.detail)
        // assertThat(retrieved.todos).extracting("date").containsExactlyInAnyOrder(todo1.date, todo2.date)

        assertThat(retrieved.errorCode).isEqualTo(0)
        assertThat(retrieved.errorMessage).isEqualTo("")
    }

    @Test
    fun testUpdateTodo() {
        // given
        val todo1 = NewTodo("title", "detail", "2020-01-01")
        post(todo1)

        // when
        val update = NewTodo("title2", "detail2", "2020-02-02")
        val updated = given()
            .contentType(ContentType.JSON)
            .body(update)
            .When()
            .put("/todos/{id}", ID_START)
            .then()
            .statusCode(200)
            .extract().to<TodoResponse>()

        // then
        assertThat(updated.errorCode).isEqualTo(0)
        assertThat(updated.errorMessage).isEqualTo("")
    }

    @Test
    fun testDeleteTodo() {
        // given
        val todo1 = NewTodo("title", "detail", "2020-01-01")
        post(todo1)

        // when
        val retrieved = delete("/todos/{id}", ID_START)
            .then()
            .statusCode(200)
            .extract().to<TodoResponse>()

        // then
        assertThat(retrieved.errorCode).isEqualTo(0)
        assertThat(retrieved.errorMessage).isEqualTo("")
    }

    @Nested
    inner class ErrorCases {

        @Test
        fun `Todo登録時にリクエストパラメータが不正の場合、エラーレスポンスを返す`() {
            // when
            val retrieved = given()
                .contentType(ContentType.JSON)
                .body("Illegal parameter")
                .When()
                .post("/todos")
                .then()
                .statusCode(400)
                .extract().to<TodoResponse>()

            // then
            assertThat(retrieved.errorCode).isEqualTo(2)
            assertThat(retrieved.errorMessage).isEqualTo("リクエストの形式が不正です")
        }

        @Test
        fun `Todo更新時にリクエストパラメータが不正の場合、エラーレスポンスを返す`() {
            // when
            val retrieved = given()
                .contentType(ContentType.JSON)
                .body("Illegal parameter")
                .When()
                .put("/todos/{id}", ID_START)
                .then()
                .statusCode(400)
                .extract().to<TodoResponse>()

            // then
            assertThat(retrieved.errorCode).isEqualTo(2)
            assertThat(retrieved.errorMessage).isEqualTo("リクエストの形式が不正です")
        }

        @Test
        fun `Todo更新時に対象のTodoが不在の場合、エラーレスポンスを返す`() {
            // when
            val todo1 = NewTodo("title", "detail", "2020-01-01")
            val retrieved = given()
                .contentType(ContentType.JSON)
                .body(todo1)
                .When()
                .put("/todos/{id}", ID_START)
                .then()
                .statusCode(500)
                .extract().to<TodoResponse>()

            // then
            assertThat(retrieved.errorCode).isEqualTo(5)
            assertThat(retrieved.errorMessage).isEqualTo("更新に失敗しました")
        }

        @Test
        fun `Todo削除時に対象のTodoが不在の場合、エラーレスポンスを返す`() {
            // when
            val retrieved = delete("/todos/{id}", ID_START)
                .then()
                .statusCode(500)
                .extract().to<TodoResponse>()

            // then
            assertThat(retrieved.errorCode).isEqualTo(6)
            assertThat(retrieved.errorMessage).isEqualTo("削除に失敗しました")
        }
    }

    private fun post(todo: NewTodo): TodoResponse {
        return given()
            .contentType(ContentType.JSON)
            .body(todo)
            .When()
            .post("/todos")
            .then()
            .statusCode(200)
            .extract().to()
    }
}
