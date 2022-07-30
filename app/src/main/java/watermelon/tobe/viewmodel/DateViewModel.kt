package watermelon.tobe.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
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

    //上方vp中存储的day
    private val _dayFragmentDays = MutableStateFlow(listOf<Day>())
    val dayFragmentDays = _dayFragmentDays.asStateFlow()
    private val _collapsedState = MutableStateFlow(CollapsedState.COLLAPSED)
    val collapsedState = _collapsedState.asStateFlow()
    private val _isTodoListChange = MutableSharedFlow<Long>()
    val isTodoListChange = _isTodoListChange.asSharedFlow()
    private val _queryTodoState = MutableStateFlow(QueryTodoState.NOT_FINISHED)
    val queryTodoState = _queryTodoState.asStateFlow()
    fun emitCollapsedState(state: CollapsedState) {
        viewModelScope.launch {
            _collapsedState.emit(state)
        }
    }

    fun emitTodoListChange() {
        viewModelScope.launch{
            //延迟一下等数据保存之后再去query，不然会查询不到新增数据
            delay(30)
            _isTodoListChange.emit(System.currentTimeMillis())
        }
    }

    fun emitDays(month: String) {
        viewModelScope.launch {
            _dayFragmentDays.emit(DateRepository.queryMonth(month))
        }
    }

    fun changeQueryTodoState() {
        if (queryTodoState.value == QueryTodoState.NOT_FINISHED) {
            viewModelScope.launch { _queryTodoState.emit(QueryTodoState.FINISHED) }
        } else {
            viewModelScope.launch { _queryTodoState.emit(QueryTodoState.NOT_FINISHED) }
        }
    }

    //上方vp的折叠状态
    enum class CollapsedState {
        EXPAND, COLLAPSED, HALF_EXPAND, SCROLLING
    }

    enum class QueryTodoState {
        FINISHED, NOT_FINISHED
    }
}