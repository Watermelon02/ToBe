package watermelon.tobe.repo.repository

import watermelon.tobe.repo.bean.Day
import watermelon.tobe.repo.database.DayDatabase
import watermelon.tobe.repo.service.HolidayService
import watermelon.tobe.util.local.DateCalculator
import java.util.*

/**
 * author : Watermelon02
 * email : 1446157077@qq.com
 * date : 2022/7/16 11:32
 */
object DateRepository {
    suspend fun queryHoliday(date: String): Day {
        val dateForLocalQuery = DateCalculator.formatDateForLocalQueryHoliday(date)
        val local = DayDatabase.getInstance().getDayDao().queryDay(dateForLocalQuery)
        if (local != null) {
            return local
        } else {
            val dateForQuery = DateCalculator.formatDateForQueryHoliday(date)
            var day :Day
            try {//尝试进行网络请求
                val remote = if (dateForQuery.split("-")[0].toInt() >= 2023) {
                    HolidayService.INSTANCE.queryHoliday(date = dateForQuery, ignoreHoliday = true)
                } else HolidayService.INSTANCE.queryHoliday(
                    date = dateForQuery,
                    ignoreHoliday = false
                )
                day = if (remote.code == 1) {
                    DayDatabase.getInstance().getDayDao().insert(remote.data)
                    remote.data
                } else {
                    generateDay(date)
                }
            } catch (e: Exception) {//网络请求失败，生成本地数据
                e.printStackTrace()
                day = generateDay(date)
            }
            return day
        }
    }

    suspend fun queryMonth(month: String): List<Day> {
        val monthForLocalQuery = DateCalculator.formatDateForLocalQueryMonth(month)
        val local = DayDatabase.getInstance().getDayDao().queryDays(monthForLocalQuery)
        var result: List<Day>
        //大于7是因为后一个月会获取前一个月的倒数几天以补满一周，所以local.size不为空，只会发送倒数几天,设置为7则会重新进行网络请求
        if (local.size > 7) {
            result = local
        } else {
            val monthForQuery = DateCalculator.formatDateForQueryMonth(month)
            result = try {//尝试进行网络请求
                val remote = if (month.split("-")[0].toInt() >= 2023) {
                    HolidayService.INSTANCE.queryMonth(month = monthForQuery, ignoreHoliday = true)
                } else HolidayService.INSTANCE.queryMonth(monthForQuery, ignoreHoliday = false)
                if (remote.code == 1) {
                    for (i in 0 until remote.data.size) {
                        DayDatabase.getInstance().getDayDao().insert(remote.data[i])
                    }
                    remote.data
                } else DateCalculator.getMonthDays(month)
            } catch (e: Exception) {//网络请求失败，生成本地数据
                e.printStackTrace()
                DateCalculator.getMonthDays(month)
            }
        }
        return result
    }

    private fun generateDay(date: String): Day {//网络请求失败时，生成本地数据
        val holidayCalendar = Calendar.getInstance()
        holidayCalendar[Calendar.YEAR] = date.split("-")[0].toInt()
        holidayCalendar[Calendar.MONTH] = date.split("-")[1].toInt() - 1
        holidayCalendar[Calendar.DATE] = date.split("-")[2].toInt()
        return Day(date = date, weekDay = holidayCalendar[Calendar.DAY_OF_WEEK] - 1)
    }


}