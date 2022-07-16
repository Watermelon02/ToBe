/*
package watermelon.tobe.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import watermelon.tobe.bean.Day


*/
/**
 * author : Watermelon02
 * email : 1446157077@qq.com
 * date : 2022/7/14 21:45
 *//*

@Database(entities = [Day::class], version = 1, exportSchema = false)
abstract class DayDatabase : RoomDatabase() {

    abstract fun getDayDao(): DayDao

    companion object {
        @Volatile
        private lateinit var database: DayDatabase

        */
/**网上很多都是将此方法与getInstance写在一起，但是那样每次获取数据库实例都需要传递context，
         * 又因为数据库访问贯穿整个app的生命周期，于是用该方法在application创建时初始化,
         * 之后获取数据库实例时就不再需要传递context*//*

        @JvmStatic
        fun createInstance(context: Context) {
            database = Room.databaseBuilder(
                context.applicationContext,
                DayDatabase::class.java,
                "day_database"
            ).build()
        }

        @JvmStatic
        @Synchronized
        fun getInstance(): DayDatabase {
            return database
        }

    }
}*/
