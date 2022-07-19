package watermelon.tobe.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import watermelon.tobe.repo.bean.Todo
import watermelon.tobe.repo.repository.TodoRepository

/**
 * author : Watermelon02
 * email : 1446157077@qq.com
 * date : 2022/7/19 12:51
 */
class DayFragmentViewModel : ViewModel() {
    val todoList = MutableStateFlow<List<Todo>>(listOf())
    fun queryTodoList(date: String) {
        viewModelScope.launch(Dispatchers.IO) {
            TodoRepository.queryTodoList(date = date).collectLatest {
                todoList.emit(it)
            }
        }
    }

    fun addTodo(title: String, content: String, date: String) {
        viewModelScope.launch(Dispatchers.IO) {
            TodoRepository.addTodo(title, content, date)
        }
    }

    fun deleteTodo(todo: Todo) {
        viewModelScope.launch(Dispatchers.IO) {
            TodoRepository.deleteTodo(todo)
        }
    }

    fun updateTodo(todo: Todo) {
        viewModelScope.launch(Dispatchers.IO) {
            TodoRepository.updateTodo(
                todo.id,
                todo.title,
                todo.content,
                todo.dateStr,
                todo.priority,
                todo.status
            )
        }
    }
}