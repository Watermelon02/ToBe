package watermelon.tobe.repo.repository

import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import watermelon.tobe.repo.bean.QueryTodoResponse
import watermelon.tobe.repo.bean.Todo
import watermelon.tobe.repo.database.TodoDatabase
import watermelon.tobe.repo.service.ToDoService
import watermelon.tobe.util.local.DateCalculator

/**
 * author : Watermelon02
 * email : 1446157077@qq.com
 * date : 2022/7/19 10:49
 */
object TodoRepository {
    //远程数据获取到之前发送的假数据
    private val loadingData = listOf(Todo(title = "Loading", content = "Todo正在飞速加载！"))
    //两端都没有Todo时发送的假数据
    private val emptyData = listOf(Todo(title = "Empty", content = "快来创建Todo吧！"))

    suspend fun addTodo(title: String, content: String, date: String, priority: Int = 0) {
        val type = DateCalculator.formatDateForQueryHoliday(date).toLong()
        try {
            val response = ToDoService.INSTANCE.addTodo(title, content, date, type, priority)
            if (response.errorCode == 0) {
                TodoDatabase.getInstance().getTodoDao().insert(response.data)
            } else {
                throw Exception(response.errorMsg)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun updateTodo(
        id: Long,
        title: String,
        content: String,
        dateStr: String,
        priority: Int = 0,
        status: Int
    ) {
        val type = DateCalculator.formatDateForQueryHoliday(dateStr).toLong()
        try {
            val response =
                ToDoService.INSTANCE.updateTodo(id, title, content, type, priority, status)
            if (response.errorCode == 0) {
                TodoDatabase.getInstance().getTodoDao().insert(response.data)
            } else {
                throw Exception(response.errorMsg)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun deleteTodo(todo: Todo) {
        try {
            ToDoService.INSTANCE.deleteTodo(todo.id)
            TodoDatabase.getInstance().getTodoDao().deleteTodo(todo)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun queryTodoList(status: Int = -1, date: String, priority: Int = 0, index: Int = 1) =
        flow {
            //转换格式
            val type = DateCalculator.formatDateForQueryHoliday(date).toLong()
            val local = TodoDatabase.getInstance().getTodoDao().queryTodos(date)
            if (local.isNotEmpty()) emit(local)
            else try {
                emit(loadingData)
                queryRemote(date, type, local, status, priority, index)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    private suspend fun FlowCollector<List<Todo>>.queryRemote(
        date: String,
        type: Long,
        local: List<Todo>,
        status: Int = -1,
        priority: Int = 0,
        index: Int = 1
    ) {
        //根据status决定是去获取已经完成的、未完成的、还是所有的Todo
        val remote = when (status) {
            0 -> ToDoService.INSTANCE.queryTodoListNotFinished(
                status,
                type,
                priority,
                index
            )
            1 -> ToDoService.INSTANCE.queryTodoListFinished(status, type, priority, index)
            else -> ToDoService.INSTANCE.queryTodoListAll(type, priority, index)
        }
        //对远端数据和本地数据进行比较，根据结果决定是否发送数据
        if (remote.errorCode == 0) {
            if (remote.data.datas.size != local.size) {
                emit(remote.data.datas)
                updateTodoList(remote, local)
            } else if (local.isEmpty()) {//都为空，则发送空数据
                emit(emptyData)
            } else {
                var isSame = true
                for (i in 0..local.size) {
                    if (remote.data.datas[i] != local[i]) {
                        isSame = false
                        break
                    }
                }
                if (!isSame) {
                    emit(remote.data.datas)
                    updateTodoList(remote, local)
                }
            }
        } else if (remote.errorCode == -1001) {//没有登录
            ToDoService.INSTANCE.login("1446157077@qq.com", "ai1wei2xi3")
            queryRemote(date, type, local, status, priority, index)
        } else {
            throw Exception(remote.errorMsg)
        }
    }

    //比对两端数据，将改变后的数据更新到数据库中
    private fun updateTodoList(remote: QueryTodoResponse, local: List<Todo>) {
        for (i in 0..local.size) {
            if (remote.data.datas[i] != local[i]) {
                TodoDatabase.getInstance().getTodoDao().insert(remote.data.datas[i])
            }
        }
    }
}