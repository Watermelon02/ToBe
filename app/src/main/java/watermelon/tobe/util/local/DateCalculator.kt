package watermelon.tobe.util.local

import android.annotation.SuppressLint
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.*

/**
 * description ： 用于计算日期和每月天数的工具类
 * author : Watermelon02
 * email : 1446157077@qq.com
 * date : 2022/7/14 14:49
 */
object DateCalculator {
    //MonthFragment上方vp会显示的月份数量
    const val TOTAL_MONTH = 25
    val calendar1 = Calendar.getInstance()
    val calendar2 = Calendar.getInstance()
    //当前选中要展示的日期
    var lastYear = calendar2[Calendar.YEAR]
    var lastMonth = calendar2[Calendar.MONTH]
    var lastDay = calendar2[Calendar.DATE]
    val days = MutableStateFlow(listOf(""))
    val viewPagerDayCurrentItem =
        MutableStateFlow("${calendar2[Calendar.YEAR]}-${calendar2[Calendar.MONTH]}-${calendar2[Calendar.DATE]}")


    //计算要查看的月份和当前选中月份的差值
    @SuppressLint("SimpleDateFormat")
    fun calculateDiffMonth(
        year: Int,
        month: Int
    ): Int {
        return if (lastMonth > month) {
            -((lastMonth - month) + 12 * (lastYear - year))
        } else {
            -((lastMonth + 12 - month) + 12 * (lastYear - year - 1))
        }
    }

    /**@param diff 要计算的月份与当前月份的差
     * @return 获取到目标月份的日期格式字符串ArrayList*/
    suspend fun getMonthDays(diff: Int) {
        val mDays = ArrayList<String>()
        calendar1.add(Calendar.MONTH,diff)
        calendar1[Calendar.DATE] = 1 //把日期设置为当月第一天
        calendar1.roll(Calendar.DATE, -1) //日期回滚一天，也就是最后一天
        for (i in 1..calendar1[Calendar.DATE]) {
            mDays.add("${calendar1[Calendar.YEAR]}-${calendar1[Calendar.MONTH]}-$i")
        }
        days.emit(mDays)
    }

    /**@param diff 要计算的月份与当前月份的差
     * @return 当前月份每天以yy-MM-DD的String存储的Arraylist*/
    fun getDays(diff: Int): ArrayList<String> {
        val mDays = ArrayList<String>()
        calendar2.add(Calendar.MONTH,diff)
        calendar2[Calendar.DATE] = 1 //把日期设置为当月第一天
        calendar2.roll(Calendar.DATE, -1) //日期回滚一天，也就是最后一天

        for (i in 1..calendar2[Calendar.DATE]) {
            mDays.add("${calendar2[Calendar.YEAR]}-${calendar2[Calendar.MONTH]}-$i")
        }
        return mDays
    }

    /**将yyyy-MM-dd的日期格式化为yyyyMMdd格式以进行网络请求*/
    fun formatDateForQueryHoliday(date:String): String {
        val list = date.split("-")
        var dateForQuery = ""
        for (i in 0..2){
            dateForQuery+=list[i]
        }
        return dateForQuery
    }

    /**将yyyy-MM-dd的日期格式化为yyyyMM格式以进行网络请求*/
    fun formatDateForQueryMonth(date:String): String {
        val list = date.split("-")
        var monthForQuery = ""
        for (i in 0..1){
            monthForQuery+=list[i]
        }
        return monthForQuery
    }
}