package watermelon.tobe.repo.repository

import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import watermelon.tobe.util.extension.toast
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
    private val loadingData by lazy { listOf(Todo(title = "Loading", content = "Todo正在飞速加载！", priority = -1)) }

    //两端都没有Todo时发送的假数据
    val emptyData by lazy { listOf(Todo(title = "Empty", content = "快来创建Todo吧！", priority = -1)) }
    val connectFailData by lazy { listOf(Todo(title = "Connect Fail", content = "网络连接失败，检查一下网络或者是否登录吧", priority = -1)) }

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
        status: Int,
    ) {
        val type = DateCalculator.formatDateForQueryHoliday(dateStr).toLong()
        try {
            val response =
                ToDoService.INSTANCE.updateTodo(id, title, content, type, priority, status,dateStr)
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
        } catch (e: Exception) {
            e.printStackTrace()
        }
        TodoDatabase.getInstance().getTodoDao().deleteTodo(todo)
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
                emit(connectFailData)
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
        val remote = ToDoService.INSTANCE.queryTodoListAll(
            status = status,
            type = type,
            priority = priority,
            index = index
        )
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
            if (UserRepository.tryLogin()) {//登录成功,再次发送请求
                queryRemote(date, type, local, status, priority, index)
            } else toast("没有登录")
        } else {
            throw Exception(remote.errorMsg)
        }
    }

    //比对两端数据，将改变后的数据更新到数据库中
    private fun updateTodoList(remote: QueryTodoResponse, local: List<Todo>) {
        if (remote.data.datas.isNotEmpty() && local.isNotEmpty()) {
            for (i in 0..local.size) {
                if (remote.data.datas[i] != local[i]) {
                    TodoDatabase.getInstance().getTodoDao().insert(remote.data.datas[i])
                }
            }
        }
    }
}