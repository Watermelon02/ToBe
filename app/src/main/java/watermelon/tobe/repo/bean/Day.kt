package watermelon.tobe.repo.bean

import androidx.room.Entity
import androidx.room.PrimaryKey
import watermelon.tobe.service.aidl.Todo

/**
 * author : Watermelon02
 * email : 1446157077@qq.com
 * date : 2022/7/14 13:26

 */
@Entity
class Day(
    val avoid: String="",
    val chineseZodiac: String="",
    val constellation: String="",
    @PrimaryKey
    val date: String="",
    val dayOfYear: Int=0,
    val indexWorkDayOfMonth: Int=0,
    val lunarCalendar: String="",
    val solarTerms: String="",
    val suit: String="",
    val type: Int=0,
    val typeDes: String="",
    val weekDay: Int=0,
    val weekOfYear: Int=0,
    val yearTips: String="",
    var todo:List<Todo> = listOf()
)