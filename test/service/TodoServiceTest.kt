package service

import com.todo.model.NewTodo
import com.todo.model.RecordInvalidException
import com.todo.model.TodosConstant.Companion.ID_START
import com.todo.service.TodoService
import common.ServerTest
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class TodoServiceTest : ServerTest() {

    private val todoService = TodoService()

    @Test
    fun getAllTodo() = runBlocking {
        assertThat(todoService.countTodo()).isEqualTo(0)

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
        // assertThat(todos).extracting("date").containsExactlyInAnyOrder(todo1.date, todo2.date, todo3.date)

        Unit
    }

    @Test
    fun addTodo() = runBlocking {
        assertThat(todoService.countTodo()).isEqualTo(0)

        // given
        val todo1 = NewTodo("title", "detail", "2020-01-01")

        // when
        todoService.addTodo(todo1)

        // then
        val retrieved = todoService.getTodo(ID_START)
        assertThat(todoService.countTodo()).isEqualTo(1)
        assertThat(retrieved?.id).isEqualTo(ID_START)
        assertThat(retrieved?.title).isEqualTo(todo1.title)
        assertThat(retrieved?.detail).isEqualTo(todo1.detail)
        // assertThat(retrieved?.date).isEqualTo(todo1.date)

        Unit
    }

    @Test
    fun updateTodo() = runBlocking {
        assertThat(todoService.countTodo()).isEqualTo(0)

        // given
        val todo1 = NewTodo("title", "detail", "2020-01-01")
        todoService.addTodo(todo1)

        // when
        val todo2 = NewTodo("updatedTitle", "updatedDetail", "2020-02-02")
        todoService.updateTodo(ID_START, todo2)

        // then
        val retrieved = todoService.getTodo(ID_START)
        assertThat(todoService.countTodo()).isEqualTo(1)
        assertThat(retrieved?.id).isEqualTo(ID_START)
        assertThat(retrieved?.title).isEqualTo(todo2.title)
        assertThat(retrieved?.detail).isEqualTo(todo2.detail)
        // assertThat(retrieved?.date).isEqualTo(todo2.date)

        Unit
    }

    @Test
    fun deleteTodo() = runBlocking {
        assertThat(todoService.countTodo()).isEqualTo(0)

        // given
        val todo1 = NewTodo("title", "detail", "2020-01-01")
        todoService.addTodo(todo1)

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
            val todo1 = NewTodo(
                "titleeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee",
                "detail",
                "2020-01-01"
            )

            // when
            val thrown: Throwable = catchThrowable {
                todoService.addTodo(todo1)
            }

            // then
            assertThat(thrown)
                .isInstanceOf(RecordInvalidException::class.java)

            Unit
        }

        @Test
        fun `Todo登録時のDetailが1000文字以上の場合、例外が発生する`() = runBlocking {
            // given
            val todo1 = NewTodo(
                "detaillllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllll",
                "detail",
                "2020-01-01"
            )

            // when
            val thrown: Throwable = catchThrowable {
                todoService.addTodo(todo1)
            }

            // then
            assertThat(thrown)
                .isInstanceOf(RecordInvalidException::class.java)

            Unit
        }

        @Test
        fun `Todo更新時に対象のTodoが存在しない場合、例外が発生する`() = runBlocking {
            // when
            val todo2 = NewTodo("updatedTitle", "updatedDetail", "2020-02-02")
            val thrown: Throwable = catchThrowable {
                todoService.updateTodo(ID_START, todo2)
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
