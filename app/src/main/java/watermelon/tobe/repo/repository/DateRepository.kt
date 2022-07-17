package watermelon.tobe.repo.repository

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import watermelon.tobe.repo.bean.Day
import watermelon.tobe.repo.bean.MonthResponse
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
    suspend fun queryHolidayForFlow(date: String) = flow {
        val dateForLocalQuery = DateCalculator.formatDateForLocalQueryHoliday(date)
        val local = DayDatabase.getInstance().getDayDao().queryDay(dateForLocalQuery)
        if (local != null) {
            emit(local)
        }
        if (local == null) {
            val dateForQuery = DateCalculator.formatDateForQueryHoliday(date)
            try {//尝试进行网络请求
                val remote = if (dateForQuery.split("-")[0].toInt() >= 2023) {
                    HolidayService.INSTANCE.queryHoliday(date = dateForQuery, ignoreHoliday = true)
                } else HolidayService.INSTANCE.queryHoliday(
                    date = dateForQuery,
                    ignoreHoliday = false
                )
                if (remote.code == 1) {
                    emit(remote.data)
                    DayDatabase.getInstance().getDayDao().insert(remote.data)
                } else emit(generateDay(date))
            } catch (e: Exception) {//网络请求失败，生成本地数据
                e.printStackTrace()
                emit(generateDay(date))
            }
        }
    }.flowOn(Dispatchers.IO)

    suspend fun queryHoliday(date: String): Day {
        val dateForLocalQuery = DateCalculator.formatDateForLocalQueryHoliday(date)
        val local = DayDatabase.getInstance().getDayDao().queryDay(dateForLocalQuery)
        if (local != null) {
            return local
        } else {
            val dateForQuery = DateCalculator.formatDateForQueryHoliday(date)
            var day = Day()
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

    suspend fun queryMonthForFlow(month: String) = flow {
        val monthForLocalQuery = DateCalculator.formatDateForLocalQueryMonth(month)
        val local = DayDatabase.getInstance().getDayDao().queryDays(monthForLocalQuery)
        if (local.isNotEmpty()) emit(local)
        if (local.isEmpty()) {
            val monthForQuery = DateCalculator.formatDateForQueryMonth(month)
            try {//尝试进行网络请求
                val remote = if (month.split("-")[0].toInt() >= 2023) {
                    HolidayService.INSTANCE.queryMonth(month = monthForQuery, ignoreHoliday = true)
                } else HolidayService.INSTANCE.queryMonth(monthForQuery, ignoreHoliday = false)
                if (remote.code == 1) {
                    emit(remote.data)
                    for (i in 0 until remote.data.size) {
                        DayDatabase.getInstance().getDayDao().insert(remote.data[i])
                    }
                }
            } catch (e: Exception) {//网络请求失败，生成本地数据
                e.printStackTrace()
                emit(DateCalculator.getMonthDays(month))
            }
        }
    }.flowOn(Dispatchers.IO)

    private fun generateDay(date: String): Day {//网络请求失败时，生成本地数据
        val holidayCalendar = Calendar.getInstance()
        holidayCalendar[Calendar.YEAR] = date.split("-")[0].toInt()
        holidayCalendar[Calendar.MONTH] = date.split("-")[1].toInt() - 1
        holidayCalendar[Calendar.DATE] = date.split("-")[2].toInt()
        return Day(date = date, weekDay = holidayCalendar[Calendar.DAY_OF_WEEK])
    }
}