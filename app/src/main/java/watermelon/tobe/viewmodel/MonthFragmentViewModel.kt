package watermelon.tobe.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import watermelon.tobe.repo.bean.Day
import watermelon.tobe.repo.repository.DateRepository
import java.util.*

/**
 * description ： TODO:类的作用
 * author : Watermelon02
 * email : 1446157077@qq.com
 * date : 2022/7/18 14:57
 */
class MonthFragmentViewModel : ViewModel() {
    val days = MutableStateFlow(listOf<Day>())
    fun emitDays(month: String) {
        viewModelScope.launch(Dispatchers.IO) {
            days.emit(getWeekDayAtNextMonth(getWeekDayAtLastMonth(DateRepository.queryMonth(month))))
        }
    }

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
                        "${calendar[Calendar.YEAR]}-${calendar[Calendar.MONTH] + 1}-$i"
                    )
                list.add(day)
            }
            list.addAll(dayList.toTypedArray())
            list.toList()
        }
    }

    //获取当前显示月份后一个月的前几天以填充日历倒数第一排刚好为一周
    private suspend fun getWeekDayAtNextMonth(dayList: List<Day>): List<Day> {
        return if (dayList.last().weekDay == 6) {
            dayList
        } else {
            val calendar = Calendar.getInstance()
            val list = arrayListOf<Day>()
            val index = if (dayList.last().weekDay < 7) {
                6 - dayList.last().weekDay
            } else {
                6
            }
            calendar[Calendar.YEAR] = dayList[0].date.split("-")[0].toInt()
            calendar[Calendar.MONTH] = dayList[0].date.split("-")[1].toInt()
            calendar[Calendar.DATE] = 1 //把日期设置为当月第一天
            list.addAll(dayList.toTypedArray())
            for (i in 1..index) {
                val day =
                    DateRepository.queryHoliday(
                        "${calendar[Calendar.YEAR]}-${calendar[Calendar.MONTH] + 2}-$i"
                    )
                list.add(day)
            }
            list.toList()
        }
    }
}