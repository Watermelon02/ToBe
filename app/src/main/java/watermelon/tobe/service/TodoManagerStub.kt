package watermelon.tobe.service

import android.app.NotificationManager
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import watermelon.tobe.repo.repository.TodoRepository
import watermelon.tobe.service.aidl.Todo
import watermelon.tobe.service.aidl.TodoManager
import watermelon.tobe.util.local.DateCalculator

/**
 * description ： Stub的具体实现类
 * author : Watermelon02
 * email : 1446157077@qq.com
 * date : 2022/7/23 21:46
 */
class TodoManagerStub(
    private val todayList: MutableStateFlow<List<Todo>>,
    private val jobList: ArrayList<Job>,
    private val manager: NotificationManager,
) : TodoManager.Stub() {
    override fun addTodo(todo: Todo?) {
        todo?.let {
            queryTodoList()
        }
    }

    override fun deleteTodo(todo: Todo?) {
        todo?.let {
            val oldList = todayList.value
            if (oldList.contains(todo)) {
                val newList = mutableListOf<Todo>()
                newList.addAll(oldList.toTypedArray())
                newList.remove(todo)
                emitTodoList(newList)
            }
        }
    }

    //当登录时，调用此方法，获取todayList
    override fun login() {
        queryTodoList()
    }

    //当推出时，调用此方法，清空todayList，并清空notification
    override fun exit() {
        val job = GlobalScope.launch(Dispatchers.IO) {
            todayList.emit(TodoRepository.emptyData)
            manager.cancelAll()
        }
        jobList.add(job)
    }

    //查询今日未完成的Todo
    fun queryTodoList() {
        val job = GlobalScope.launch {
            TodoRepository.queryTodoList(status = 0, date = DateCalculator.todayDate)
                .collectLatest {
                    if (it.isNotEmpty() && it[0].priority != -1) {//排除发送Loading,Connect_Fail等状况
                        todayList.emit(it)
                    }
                }
        }
        jobList.add(job)
    }

    private fun emitTodoList(newList: List<Todo>) {
        val job = GlobalScope.launch {
            todayList.emit(newList.toList())
        }
        jobList.add(job)
    }
}