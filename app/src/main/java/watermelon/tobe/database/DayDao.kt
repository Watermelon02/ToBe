package watermelon.tobe.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import watermelon.tobe.bean.Day

/**
 * author : Watermelon02
 * email : 1446157077@qq.com
 * date : 2022/7/14 13:35
 */
@Dao
interface DayDao {
    @Query("SELECT * FROM day WHERE time =:time")
    fun queryDay(time:String): Flow<Day?>

    @Query("SELECT * FROM day WHERE time Like :time")
    fun queryDays(time:String): List<Day?>

    @Delete
    fun deleteDay(day: Day)

    @Insert
    fun insert(day: Day)

    @Update
    fun update(day: Day)
}