package watermelon.tobe.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import watermelon.tobe.repo.repository.TodoRepository
import watermelon.tobe.util.local.DateCalculator

/**
 * author : Watermelon02
 * email : 1446157077@qq.com
 * date : 2022/7/21 11:31
 */
class AddTodoFragmentViewModel: ViewModel() {
    var year = 0
    var month = 0
    var day = 0
    var title = ""
    var content=""

    fun addTodo() {
        viewModelScope.launch(Dispatchers.IO) {
            val date = DateCalculator.formatDateForLocalQueryHoliday("${year}-${month}-${day}")
            TodoRepository.addTodo(title, content, date)
            year = 0
            month = 0
            day = 0
            this@AddTodoFragmentViewModel.title = ""
            this@AddTodoFragmentViewModel.content=""
        }
    }
}