package service

import com.todo.model.NewTodo
import com.todo.service.TodoService
import common.ServerTest
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class TodoServiceTest : ServerTest() {

    private val todoService = TodoService()

    @Test
    fun addTodo() = runBlocking {
        // given
        val todo1 = NewTodo("title", "detail", "2020-01-01")

        // when
        val saved = todoService.addTodo(todo1)

        // then
        val retrieved = todoService.getTodo(saved.id)
        assertThat(retrieved).isEqualTo(saved)
        assertThat(retrieved?.title).isEqualTo(todo1.title)
        assertThat(retrieved?.detail).isEqualTo(todo1.detail)
        // assertThat(retrieved?.date).isEqualTo(todo1.date)

        Unit
    }
}
