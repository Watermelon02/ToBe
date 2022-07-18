package watermelon.tobe.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import watermelon.tobe.repo.bean.Day
import watermelon.tobe.repo.repository.DateRepository
import watermelon.tobe.util.local.DateCalculator
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
    var collapsedState = MutableStateFlow(CollapsedState.COLLAPSED)

    suspend fun queryHoliday(date: String) = DateRepository.queryHolidayForFlow(date).catch {
        Log.d("testTag", "(DateViewModel.kt:26) -> ${it.message}")
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        null
    )


    suspend fun queryMonth(month: String) =
        DateRepository.queryMonthForFlow(month).map {
            withContext(viewModelScope.coroutineContext + Dispatchers.IO) {
                getWeekDayAtLastMonth(it)
            }
        }.catch {
            Log.d("testTag", "(DateViewModel.kt:38) -> ${it.message}")
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            null
        )

    //获取当前显示月份前一个月的倒数几天以填充日历第一排刚好为一周
    private suspend fun getWeekDayAtLastMonth(dayList: List<Day>): List<Day> {
        return if (dayList[0].weekDay == 7) {
            dayList
        } else {
            val calendar = Calendar.getInstance()
            val list = arrayListOf<Day>()
            calendar[Calendar.YEAR] = dayList[0].date.split("-")[0].toInt()
            calendar[Calendar.MONTH] = dayList[0].date.split("-")[1].toInt() - 2
            calendar[Calendar.DATE] = 1 //把日期设置为当月第一天
            calendar.roll(Calendar.DATE, -1) //日期回滚一天，也就是最后一天
            for (i in calendar[Calendar.DATE] - dayList[0].weekDay + 1..calendar[Calendar.DATE]) {
                val day =
                    DateRepository.queryHoliday(
                        "${calendar[Calendar.YEAR]}-${calendar[Calendar.MONTH] +1}-$i"
                    )
                list.add(day)
            }
            list.addAll(dayList.toTypedArray())
            list.toList()
        }
    }

    //上方vp的折叠状态
    enum class CollapsedState {
        EXPAND, COLLAPSED, HALF_EXPAND
    }
}