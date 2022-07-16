package watermelon.tobe.viewmodel

import androidx.lifecycle.ViewModel
import watermelon.tobe.service.HolidayService
import watermelon.tobe.util.local.DateCalculator

/**
 * author : Watermelon02
 * email : 1446157077@qq.com
 * date : 2022/7/14 14:32
 */
class DateViewModel  : ViewModel() {

    suspend fun queryHoliday(date:String){
        val dateForQuery = DateCalculator.formatDateForQueryHoliday(date)
        HolidayService.INSTANCE.queryHoliday(dateForQuery)
    }

    suspend fun queryMonth(month:String){
        val monthForQuery = DateCalculator.formatDateForQueryMonth(month)
        HolidayService.INSTANCE.queryMonth(monthForQuery)
    }

//    fun queryDay(time: String) = dayDatabase.getDayDao().queryDay(time)
}