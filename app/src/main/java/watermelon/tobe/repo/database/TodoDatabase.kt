package watermelon.tobe.repo.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import watermelon.tobe.repo.bean.Todo

/**
 * author : Watermelon02
 * email : 1446157077@qq.com
 * date : 2022/7/19 10:46
 */
@Database(entities = [Todo::class], version = 1, exportSchema = false)
abstract class TodoDatabase : RoomDatabase() {
    abstract fun getTodoDao(): TodoDao

    companion object {
        @Volatile
        private lateinit var database: TodoDatabase

        /**网上很多都是将此方法与getInstance写在一起，但是那样每次获取数据库实例都需要传递context，
         * 又因为数据库访问贯穿整个app的生命周期，于是用该方法在application创建时初始化,
         * 之后获取数据库实例时就不再需要传递context*/

        @JvmStatic
        fun createInstance(context: Context) {
            database = Room.databaseBuilder(
                context.applicationContext,
                TodoDatabase::class.java,
                "Todo_database"
            ).build()
        }

        @JvmStatic
        @Synchronized
        fun getInstance(): TodoDatabase {
            return database
        }

    }
}