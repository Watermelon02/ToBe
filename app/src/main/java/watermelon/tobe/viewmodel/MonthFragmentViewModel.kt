package watermelon.tobe.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import watermelon.tobe.repo.bean.Day
import watermelon.tobe.repo.repository.DateRepository

/**
 * description ： TODO:类的作用
 * author : Watermelon02
 * email : 1446157077@qq.com
 * date : 2022/7/18 14:57
 */
class MonthFragmentViewModel: ViewModel() {
    val days = MutableStateFlow(listOf<Day>())
    fun emitDays(month: String) {
        viewModelScope.launch(Dispatchers.IO) {
            days.emit(DateRepository.queryMonth(month))
        }
    }
}