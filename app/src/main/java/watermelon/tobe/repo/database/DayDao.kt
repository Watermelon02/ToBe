package watermelon.tobe.repo.database

import androidx.room.*
import watermelon.tobe.repo.bean.Day

/**
 * author : Watermelon02
 * email : 1446157077@qq.com
 * date : 2022/7/14 13:35
 */
@Dao
interface DayDao {
    @Query("SELECT * FROM day WHERE date =:date")
    fun queryDay(date: String): Day?

    @Query("SELECT * FROM day WHERE date LIKE '%'|| :date ||'%'")
    fun queryDays(date: String): List<Day>

    @Query("SELECT * FROM day")
    fun queryAllDays(): List<Day>

    @Delete
    fun deleteDay(day: Day)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(day: Day)

    @Update
    fun update(day: Day)
}