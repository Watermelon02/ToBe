package watermelon.tobe.repo.repository

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import watermelon.tobe.repo.database.DayDatabase
import watermelon.tobe.repo.service.HolidayService
import watermelon.tobe.util.local.DateCalculator

/**
 * author : Watermelon02
 * email : 1446157077@qq.com
 * date : 2022/7/16 11:32
 */
object DateRepository {
    suspend fun queryHoliday(date: String) = flow {
        val dateForLocalQuery = DateCalculator.formatDateForLocalQueryHoliday(date)
        val local = DayDatabase.getInstance().getDayDao().queryDay(dateForLocalQuery)
        if (local != null) {
            emit(local)
        }
        if (local == null) {
            val dateForQuery = DateCalculator.formatDateForQueryHoliday(date)
            val remote = if (dateForQuery.split("-")[0].toInt() >= 2023) {
                HolidayService.INSTANCE.queryHoliday(date = dateForQuery, ignoreHoliday = true)
            } else HolidayService.INSTANCE.queryHoliday(date = dateForQuery, ignoreHoliday = false)
            if (remote.code == 1) {
                emit(remote.data)
                DayDatabase.getInstance().getDayDao().insert(remote.data)
            }
        }
    }.flowOn(Dispatchers.IO)

    suspend fun queryMonth(month: String) = flow {
        val monthForLocalQuery = DateCalculator.formatDateForLocalQueryMonth(month)
        val local = DayDatabase.getInstance().getDayDao().queryDays(monthForLocalQuery)
        if (local.isNotEmpty()) emit(local)
        if (local.isEmpty()) {
            val monthForQuery = DateCalculator.formatDateForQueryMonth(month)
            val remote = if (month.split("-")[0].toInt() >= 2023) {
                HolidayService.INSTANCE.queryMonth(month = monthForQuery, ignoreHoliday = true)
            } else HolidayService.INSTANCE.queryMonth(monthForQuery, ignoreHoliday = false)
            if (remote.code == 1) {
                emit(remote.data)
                for (i in 0 until remote.data.size) {
                    DayDatabase.getInstance().getDayDao().insert(remote.data[i])
                }
            }
        }
    }.flowOn(Dispatchers.IO)
}