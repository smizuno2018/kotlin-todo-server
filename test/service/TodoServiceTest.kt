package service

import com.todo.model.NewTodo
import com.todo.model.RecordInvalidException
import com.todo.model.TodosConstant.Companion.ID_START
import com.todo.service.TodoService
import common.ServerTest
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class TodoServiceTest : ServerTest() {

    private val todoService = TodoService()

    @BeforeEach
    fun `各テストはTodoレコード0件を前提に作成しているため、レコード数を確認する`() {
        assertThat(todoService.countTodo()).isEqualTo(0)
    }

    @Test
    fun getAllTodo() = runBlocking {
        // given
        val todo1 = NewTodo("title", "detail", "2020-01-01")
        val todo2 = NewTodo("title", "detail", "2020-01-02")
        val todo3 = NewTodo("title", "detail", "2020-01-03")
        todoService.addTodo(todo1)
        todoService.addTodo(todo2)
        todoService.addTodo(todo3)

        // when
        val retrieved = todoService.getAllTodo()

        // then
        assertThat(todoService.countTodo()).isEqualTo(3)
        assertThat(retrieved).extracting("title").containsExactlyInAnyOrder(todo1.title, todo2.title, todo3.title)
        assertThat(retrieved).extracting("detail").containsExactlyInAnyOrder(todo1.detail, todo2.detail, todo3.detail)
        // assertThat(retrieved).extracting("date").containsExactlyInAnyOrder(todo1.date, todo2.date, todo3.date)

        Unit
    }

    @Test
    fun addTodo() = runBlocking {
        // given
        val newTodo = NewTodo("title", "detail", "2020-01-01")

        // when
        todoService.addTodo(newTodo)

        // then
        val retrieved = todoService.getTodo(ID_START)
        assertThat(todoService.countTodo()).isEqualTo(1)
        assertThat(retrieved?.id).isEqualTo(ID_START)
        assertThat(retrieved?.title).isEqualTo(newTodo.title)
        assertThat(retrieved?.detail).isEqualTo(newTodo.detail)
        // assertThat(retrieved?.date).isEqualTo(todo1.date)

        Unit
    }

    @Test
    fun updateTodo() = runBlocking {
        // given
        val oldTodo = NewTodo("title", "detail", "2020-01-01")
        todoService.addTodo(oldTodo)

        // when
        val newTodo = NewTodo("updatedTitle", "updatedDetail", "2020-02-02")
        todoService.updateTodo(ID_START, newTodo)

        // then
        val retrieved = todoService.getTodo(ID_START)
        assertThat(todoService.countTodo()).isEqualTo(1)
        assertThat(retrieved?.id).isEqualTo(ID_START)
        assertThat(retrieved?.title).isEqualTo(newTodo.title)
        assertThat(retrieved?.detail).isEqualTo(newTodo.detail)
        // assertThat(retrieved?.date).isEqualTo(todo2.date)

        Unit
    }

    @Test
    fun deleteTodo() = runBlocking {
        // given
        val newTodo = NewTodo("title", "detail", "2020-01-01")
        todoService.addTodo(newTodo)

        // when
        todoService.deleteTodo(ID_START)

        // then
        assertThat(todoService.countTodo()).isEqualTo(0)

        Unit
    }

    @Nested
    inner class ErrorCases {

        @Test
        fun `Todo登録時のTitleが100文字以上の場合、例外が発生する`() = runBlocking {
            // given
            val invalidTitle =
                "titleeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee"
            assertThat(invalidTitle.length).isEqualTo(101)
            val newTodo = NewTodo(invalidTitle, "detail", "2020-01-01")

            // when
            val thrown: Throwable = catchThrowable {
                todoService.addTodo(newTodo)
            }

            // then
            assertThat(thrown)
                .isInstanceOf(RecordInvalidException::class.java)

            Unit
        }

        @Test
        fun `Todo登録時のDetailが1000文字以上の場合、例外が発生する`() = runBlocking {
            // given
            val invalidDetail =
                "detaillllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllll"
            assertThat(invalidDetail.length).isEqualTo(1001)
            val newTodo = NewTodo("title", invalidDetail, "2020-01-01")

            // when
            val thrown: Throwable = catchThrowable {
                todoService.addTodo(newTodo)
            }

            // then
            assertThat(thrown)
                .isInstanceOf(RecordInvalidException::class.java)

            Unit
        }

        @Test
        fun `Todo更新時に対象のTodoが存在しない場合、例外が発生する`() = runBlocking {
            // when
            val newTodo = NewTodo("updatedTitle", "updatedDetail", "2020-02-02")
            val thrown: Throwable = catchThrowable {
                todoService.updateTodo(ID_START, newTodo)
            }

            // then
            assertThat(thrown)
                .isInstanceOf(RecordInvalidException::class.java)

            Unit
        }

        @Test
        fun `Todo削除時に対象のTodoが存在しない場合、例外が発生する`() = runBlocking {
            // when
            val thrown: Throwable = catchThrowable {
                todoService.deleteTodo(ID_START)
            }

            // then
            assertThat(thrown)
                .isInstanceOf(RecordInvalidException::class.java)

            Unit
        }
    }
}
