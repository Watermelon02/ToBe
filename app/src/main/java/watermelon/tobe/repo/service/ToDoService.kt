package watermelon.tobe.repo.service

import retrofit2.http.*
import watermelon.tobe.repo.bean.TodoResponse
import watermelon.tobe.repo.bean.LoginResponse
import watermelon.tobe.repo.bean.QueryTodoResponse
import watermelon.tobe.repo.network.ToDoApiGenerator

/**
 * description ： Todo接口对应的Service
 * author : Watermelon02
 * email : 1446157077@qq.com
 * date : 2022/7/18 23:01
 */
interface ToDoService {
    @POST("user/login")
    suspend fun login(
        @Query("username") username: String,
        @Query("password") password: String
    ): LoginResponse

    @POST("user/register")
    suspend fun register(
        @Query("username") username: String,
        @Query("password") password: String,
        @Query(("repassword")) repassword: String
    ):LoginResponse

    @GET("user/logout/json")
    suspend fun exit()

    /**@param type type 可以用于，在app 中预定义几个类别,此处将type设置为yyyyMMdd格式的Long,便于在查询列表时作为参数传入
     * @param priority priority 主要用于定义优先级，在app 中预定义几个优先级*/
    @POST("lg/todo/add/json")
    suspend fun addTodo(
        @Query("title") title: String,
        @Query("content") content: String,
        @Query("data") date: String,
        @Query("type") type: Long,
        @Query("priority") priority: Int = 0
    ): TodoResponse

    /**@param status 0为未完成，1为完成*/
    @POST("lg/todo/update/83/json")
    suspend fun updateTodo(
        @Query("id") id: Long,
        @Query("title") title: String,
        @Query("content") content: String,
        @Query("type") type: Long,
        @Query("priority") priority: Int = 0,
        @Query("status") status: Int,
        @Query("date") dateStr: String
    ): TodoResponse

    @POST("lg/todo/delete/{id}/json")
    suspend fun deleteTodo(@Path("id") id: Long)

    /**@param index 页码*/
    @POST("lg/todo/v2/list/{index}/json")
    suspend fun queryTodoListAll(
        @Path("index") index: Int,
        @Query("status") status: Int = -1,
        @Query("type") type: Long,
        @Query("priority") priority: Int
    ): QueryTodoResponse

    companion object {
        val INSTANCE by lazy { ToDoApiGenerator.getApiService(ToDoService::class) }
    }
}