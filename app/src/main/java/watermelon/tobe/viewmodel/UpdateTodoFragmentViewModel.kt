package watermelon.tobe.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import watermelon.tobe.repo.repository.TodoRepository
import watermelon.tobe.service.aidl.Todo
import watermelon.tobe.util.local.DateCalculator

/**
 * author : Watermelon02
 * email : 1446157077@qq.com
 * date : 2022/7/21 17:06
 */
class UpdateTodoFragmentViewModel : ViewModel() {
    var todo = Todo()
    var year = 0
    var month = 0
    var day = 0
    var hour = 0
    fun updateTodo() {
        viewModelScope.launch {
            TodoRepository.updateTodo(
                todo.id,
                todo.title,
                todo.content,
                DateCalculator.formatDateForLocalQueryHoliday("${year}-${month}-${day}"),
                hour,
                todo.status,
            )
        }
    }
}