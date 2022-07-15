package watermelon.tobe.bean

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * author : Watermelon02
 * email : 1446157077@qq.com
 * date : 2022/7/14 13:26
 * @param time 发布时间,用SimpleDateFormat将System.currentTime以yyyy-MM-dd格式存储
 * @param state 心情状态，由StateButton确定
 */
@Entity(tableName = "day")
class Day(
    val time: String,
    var content: String,
    var state: String,
    @PrimaryKey(autoGenerate = true) val id: Long = 0
)