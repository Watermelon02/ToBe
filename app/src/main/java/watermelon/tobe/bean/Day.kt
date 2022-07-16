package watermelon.tobe.bean

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * author : Watermelon02
 * email : 1446157077@qq.com
 * date : 2022/7/14 13:26

 */
class Day(
    val avoid: String,
    val chineseZodiac: String,
    val constellation: String,
    val date: String,
    val dayOfYear: Int,
    val indexWorkDayOfMonth: Int,
    val lunarCalendar: String,
    val solarTerms: String,
    val suit: String,
    val type: Int,
    val typeDes: String,
    val weekDay: Int,
    val weekOfYear: Int,
    val yearTips: String
)