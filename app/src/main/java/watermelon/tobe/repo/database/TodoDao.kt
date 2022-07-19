package watermelon.tobe.repo.database

import androidx.room.*
import watermelon.tobe.repo.bean.Todo

/**
 * author : Watermelon02
 * email : 1446157077@qq.com
 * date : 2022/7/19 10:43
 */
@Dao
interface TodoDao {
    @Query("SELECT * FROM todo WHERE date =:date")
    fun queryTodo(date: String): Todo?

    @Query("SELECT * FROM todo WHERE date LIKE '%'|| :date ||'%'")
    fun queryTodos(date: String): List<Todo>

    @Query("SELECT * FROM todo")
    fun queryAllTodos(): List<Todo>

    @Delete
    fun deleteTodo(Todo: Todo)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(Todo: Todo)

    @Update
    fun update(Todo: Todo)
}