package watermelon.tobe.repo.repository

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

    suspend fun queryTodoList(status: Int = -1, date: String, priority: Int = 0, index: Int = 1) =
        flow {
            //转换格式
            val type = DateCalculator.formatDateForQueryHoliday(date).toLong()
            val local = TodoDatabase.getInstance().getTodoDao().queryTodos(date)
            if (local.isNotEmpty()) emit(local)
            else try {
                //根据status决定是获取已经完成的、未完成的、还是所有的Todo
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
                } else {
                    throw Exception(remote.errorMsg)
                }
            } catch (e: Exception) {
                e.printStackTrace()
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