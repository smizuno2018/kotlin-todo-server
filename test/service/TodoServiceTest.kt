package service

import com.todo.model.NewTodo
import com.todo.model.TodosConstant.Companion.ID_START
import com.todo.service.TodoService
import common.ServerTest
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class TodoServiceTest : ServerTest() {

    private val todoService = TodoService()

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
        // assertThat(retrieved?.date).isEqualTo(todo2.date)

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
}
