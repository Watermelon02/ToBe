package watermelon.tobe.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import watermelon.tobe.repo.bean.Day
import watermelon.tobe.repo.repository.DateRepository
import java.util.*

/**
 * author : Watermelon02
 * email : 1446157077@qq.com
 * date : 2022/7/14 14:32
 */
class DateViewModel : ViewModel() {
    //上方vp选中的月份及年份
    var currentYear = Calendar.getInstance()[Calendar.YEAR]
    var currentMonth = Calendar.getInstance()[Calendar.MONTH] + 1
    var collapsedState = MutableStateFlow(CollapsedState.HALF_EXPAND)

    //上方vp中存储的day
    val dayFragmentDays = MutableStateFlow(listOf<Day>())

    fun emitDays(month: String) {
        viewModelScope.launch(Dispatchers.IO) {
            dayFragmentDays.emit(DateRepository.queryMonth(month))
        }
    }

    fun emitCollapsedState(state: CollapsedState) {
        viewModelScope.launch {
            collapsedState.emit(state)
        }
    }

    //上方vp的折叠状态
    enum class CollapsedState {
        EXPAND, COLLAPSED, HALF_EXPAND
    }
}