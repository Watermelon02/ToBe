package watermelon.tobe.repo.database

import androidx.room.*
import watermelon.tobe.service.aidl.Todo

/**
 * author : Watermelon02
 * email : 1446157077@qq.com
 * date : 2022/7/19 10:43
 */
@Dao
interface TodoDao {
    @Query("SELECT * FROM todo WHERE date =:date")
    suspend fun queryTodo(date: String): Todo?

    @Query("SELECT * FROM todo WHERE date LIKE '%'|| :date ||'%'")
    suspend fun queryTodos(date: String): List<Todo>

    @Query("SELECT * FROM todo")
    suspend fun queryAllTodos(): List<Todo>

    @Delete
    suspend fun deleteTodo(Todo: Todo)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(Todo: Todo)

    @Update
    suspend fun update(Todo: Todo)
}