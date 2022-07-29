package watermelon.tobe.service

import android.app.*
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import androidx.annotation.RequiresApi
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import watermelon.tobe.R
import watermelon.tobe.service.aidl.Todo
import watermelon.tobe.ui.activity.DateActivity
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
class TodoManagerService : Service() {
    //当日的Todo list
    companion object {
        const val NOT_FINISH = 0
        const val FINISHED_ALL = 1
        const val OUT_OF_DATE = 2
        const val EXPIRING_SOON = 3
        const val MINUTE = 60000L
        const val INTERVAL = 10 * MINUTE
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

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate() {
        super.onCreate()
        val job = GlobalScope.launch(Dispatchers.IO) {
            todayList.collectLatest {
                if (it.isNotEmpty()) createNotFinishedNotification(it) else createFinishedNotification()
            }
        }
        jobList.add(job)
        val job2 = GlobalScope.launch(Dispatchers.IO) {
            flow {
                for (i in 0L..Long.MAX_VALUE) {
                    //每半分钟检查一次，发送通知
                    emit(i)
                    delay(MINUTE/2)
                }
            }.collectLatest {
                val calendar = Calendar.getInstance()
                for (i in 0 until todayList.value.size) {
                    calendar[Calendar.HOUR] = todayList.value[i].priority
                    calendar[Calendar.MINUTE] = 0
                    if (calendar.timeInMillis < System.currentTimeMillis()) {
                        //todo超时
                        createOutOfDateNotification(todayList.value[i],
                            System.currentTimeMillis() - calendar.timeInMillis)
                    } else if (calendar.timeInMillis - System.currentTimeMillis() in 1 until INTERVAL) {
                        createExpiringSoonNotification(todayList.value[i],
                            calendar.timeInMillis-System.currentTimeMillis())
                    }
                }
            }
        }
        jobList.add(job2)
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

    private fun createNotFinishedNotification(todoList: List<Todo>) {
        manager.createNotificationChannel(channel)
        val notification = Notification.Builder(this, channel.id)
            .setContentTitle("今日还有Todo未完成哦")
            .setContentText("今天有${todoList.size}个Todo即将截止，抓紧时间完成吧！").createNotification()
        manager.cancel(FINISHED_ALL)
        manager.notify(NOT_FINISH, notification)
    }

    private fun createFinishedNotification() {
        manager.createNotificationChannel(channel)
        val notification = Notification.Builder(this, channel.id)
            .setContentTitle("今日所有Todo已经完成！")
            .setContentText("休息一下，或者再来点吧！").createNotification()
        manager.cancel(NOT_FINISH)
        manager.notify(FINISHED_ALL, notification)
    }

    private fun createOutOfDateNotification(todo: Todo, time: Long) {
        val hour = time / (MINUTE*60)
        val minute = (time - (hour*60* MINUTE))/ MINUTE
        val text = "${todo.title}  已经在${hour}小时${minute}分钟前过期\ncontent:\n  ${todo.content}"
        //传入营业进度，生成相应的Notification对象
        manager.createNotificationChannel(channel)
        val notification = Notification.Builder(this, channel.id)
            .setContentTitle("有Todo已经过期")
            .setStyle(Notification.BigTextStyle().bigText(text)).createNotification()
        manager.notify(OUT_OF_DATE, notification)
    }

    private fun createExpiringSoonNotification(todo: Todo, time: Long) {
        val hour = time / (MINUTE*60)
        val minute = (time - (hour*60* MINUTE))/ MINUTE
        val text = "${todo.title}  还有${hour}小时${minute}分钟截止,抓紧时间完成吧！\ncontent:\n" +
                "  ${todo.content}"
        //传入营业进度，生成相应的Notification对象
        manager.createNotificationChannel(channel)
        val notification = Notification.Builder(this, channel.id)
            .setContentTitle("有Todo即将到期")
            .setStyle(Notification.BigTextStyle().bigText(text)).createNotification()
        manager.notify(EXPIRING_SOON, notification)
    }

    private fun Notification.Builder.createNotification(): Notification {
        return setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_logo1))
            .setSmallIcon(R.drawable.ic_logo)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
    }

}
