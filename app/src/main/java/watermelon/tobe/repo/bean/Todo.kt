package watermelon.tobe.repo.bean

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * description ： TODO:类的作用
 * author : Watermelon02
 * email : 1446157077@qq.com
 * date : 2022/7/19 10:30
 */
@Entity
data class Todo(
    val completeDate: String = "",
    val completeDateStr: String = "",
    val content: String = "",
    val date: Long = 0,
    val dateStr: String = "",
    @PrimaryKey
    val id: Long = 0,
    val priority: Int = 0,
    val status: Int = 0,
    val title: String = "",
    val type: Long = 0,
    val userId: Int = 0
)