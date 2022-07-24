package watermelon.tobe.service

import android.app.*
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import watermelon.tobe.R
import watermelon.tobe.service.aidl.Todo
import watermelon.tobe.ui.activity.DateActivity

@RequiresApi(Build.VERSION_CODES.O)
class TodoManagerService : Service() {
    //当日的Todo list
    companion object {
        const val NOT_FINISH = 0
        const val FINISHED_ALL = 1
    }
    private var todayList = MutableStateFlow<List<Todo>>(listOf())
    private var jobList = arrayListOf<Job>()
    private val manager by lazy { getSystemService(NOTIFICATION_SERVICE) as NotificationManager }
    private val channel by lazy {
        NotificationChannel("42",
            "Todo",
            NotificationManager.IMPORTANCE_HIGH)
    }
    private val pendingIntent: PendingIntent by lazy {
        PendingIntent.getActivity(this,
            0,
            Intent(this, DateActivity::class.java),
            0)
    }
    private val iBinder by lazy { TodoManagerStub(todayList, jobList, manager) }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        val job = GlobalScope.launch(Dispatchers.IO) {
            todayList.collectLatest {
                if (it.isNotEmpty())createNotFinishedNotification(it)else createFinishedNotification()
            }
        }
        jobList.add(job)
    }

    override fun onBind(intent: Intent?): IBinder {
        iBinder.queryTodoList()
        return iBinder
    }

    override fun onDestroy() {
        super.onDestroy()
        for (i in 0 until jobList.size) {
            jobList[i].cancel()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createNotFinishedNotification(todoList: List<Todo>) {
        //传入营业进度，生成相应的Notification对象
        manager.createNotificationChannel(channel)
        val notification = Notification.Builder(this, channel.id)
            .setContentTitle("今日还有Todo未完成哦")
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_logo1))
            .setSmallIcon(R.drawable.ic_logo)
            .setContentIntent(pendingIntent)
            .setContentText("今天有${todoList.size}个Todo即将截止，抓紧时间完成吧！")
            .setAutoCancel(true)
            .build()
        manager.cancel(FINISHED_ALL)
        manager.notify(NOT_FINISH,notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createFinishedNotification() {
        //传入营业进度，生成相应的Notification对象
        manager.createNotificationChannel(channel)
        val notification = Notification.Builder(this, channel.id)
            .setContentTitle("今日所有Todo已经完成！")
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_logo1))
            .setSmallIcon(R.drawable.ic_logo)
            .setContentIntent(pendingIntent)
            .setContentText("休息一下，或者再来点吧！")
            .setAutoCancel(true)
            .build()
        manager.cancel(NOT_FINISH)
        manager.notify(FINISHED_ALL,notification)
    }

}
