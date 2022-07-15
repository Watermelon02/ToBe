package watermelon.lightmusic.base

import android.app.Application
import android.util.Log
import kotlin.system.exitProcess

/**
 * description ： 用于处理全局报错
 * author : Watermelon02
 * email : 1446157077@qq.com
 * date : 2022/7/13 18:50
 */
object CrashHandler:Thread.UncaughtExceptionHandler {

    override fun uncaughtException(t: Thread, e: Throwable) {
        e.printStackTrace()
        exitProcess(0)
    }
}