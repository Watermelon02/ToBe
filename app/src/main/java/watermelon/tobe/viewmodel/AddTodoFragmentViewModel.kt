package watermelon.tobe.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import watermelon.tobe.base.BaseApp
import watermelon.tobe.repo.repository.TodoRepository
import watermelon.tobe.service.aidl.Todo
import watermelon.tobe.util.local.DateCalculator

/**
 * author : Watermelon02
 * email : 1446157077@qq.com
 * date : 2022/7/21 11:31
 */
class AddTodoFragmentViewModel : ViewModel() {
    var year = 0
    var month = 0
    var day = 0
    var hour = 0
    var date = ""
    var title = ""
    var content = ""
    private val _isShowing = MutableStateFlow(false)
    val isShowing = _isShowing.asStateFlow()

    fun addTodo() {
        viewModelScope.launch(Dispatchers.IO) {
            date = DateCalculator.formatDateForLocalQueryHoliday("${year}-${month}-${day}")
            TodoRepository.addTodo(title, content, date, priority = hour)
            //如果是增加的本日todo,通过binder通知TodoManagerService
            if (date == DateCalculator.todayDate) {
                BaseApp.todoManagerBinder?.addTodo(Todo(title = title,
                    content = content,
                    dateStr = date,
                    priority = hour))
            }
            year = 0
            month = 0
            day = 0
            this@AddTodoFragmentViewModel.title = ""
            this@AddTodoFragmentViewModel.content = ""
        }
    }

    fun emitShowingState(boolean: Boolean) {
        viewModelScope.launch {
            _isShowing.emit(boolean)
        }
    }
}