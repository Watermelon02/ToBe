package watermelon.tobe.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
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
            TodoRepository.queryTodoList(date = date, status = 0).collectLatest {
                todoList.emit(it)
            }
        }
    }

    fun deleteTodo(todo: Todo) {
        viewModelScope.launch(Dispatchers.IO) {
            if (todo.priority!=-1){//用if排除Empty,Connect Fail,Loading状态的T odo
                TodoRepository.deleteTodo(todo)
                todoList.update {
                    for (i in 0..it.size) {
                        if (it[i] == todo) return@update it.subList(0, i) + it.subList(i + 1, it.size)
                    }
                    return@update it
                }
            }
        }
    }

    //完成todo
    fun finishTodo(todo: Todo) {
        viewModelScope.launch(Dispatchers.IO) {
            if (todo.priority!=-1){//用if排除Empty,Connect Fail,Loading状态的T odo
                TodoRepository.updateTodo(
                    id = todo.id,
                    title = todo.title,
                    content = todo.content,
                    dateStr = todo.dateStr,
                    priority = todo.priority,
                    status = 1
                )
                delay(10)
                TodoRepository.queryTodoList(date = todo.dateStr, status = 0).collectLatest {
                    todoList.emit(it)
                }
            }
        }
    }
}