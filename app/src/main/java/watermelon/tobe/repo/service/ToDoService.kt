package watermelon.tobe.repo.service

import retrofit2.http.*
import watermelon.tobe.repo.bean.TodoResponse
import watermelon.tobe.repo.bean.LoginResponse
import watermelon.tobe.repo.bean.QueryTodoResponse
import watermelon.tobe.util.network.ToDoApiGenerator

/**
 * description ： Todo接口对应的Service
 * author : Watermelon02
 * email : 1446157077@qq.com
 * date : 2022/7/18 23:01
 */
interface ToDoService {
    @FormUrlEncoded
    @POST("user/login")
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): LoginResponse

    @FormUrlEncoded
    @POST("user/register")
    suspend fun register(
        @Field("username") username: String,
        @Field("password") password: String,
        @Field(("repassword")) repassword: String
    )

    @GET("user/logout/json")
    suspend fun exit()

    /**@param type type 可以用于，在app 中预定义几个类别,此处将type设置为yyyyMMdd格式的Long,便于在查询列表时作为参数传入
     * @param priority priority 主要用于定义优先级，在app 中预定义几个优先级*/
    @FormUrlEncoded
    @POST("lg/todo/add/json")
    suspend fun addTodo(
        @Field("title") title: String,
        @Field("content") content: String,
        @Field("data") date: String,
        @Field("type") type: Long,
        @Field("priority") priority: Int = 0
    ): TodoResponse

    /**@param status 0为未完成，1为完成*/
    @FormUrlEncoded
    @POST("lg/todo/update/83/json")
    suspend fun updateTodo(
        @Field("id") id: Long,
        @Field("title") title: String,
        @Field("content") content: String,
        @Field("type") type: Long,
        @Field("priority") priority: Int = 0,
        @Field("status") status: Int,
    ): TodoResponse

    @FormUrlEncoded
    @POST("lg/todo/delete/83/json")
    suspend fun deleteTodo(@Field("id") id: Long)

    /**@param index 页码*/
    @FormUrlEncoded
    @POST("lg/todo/v2/list/{index}/json")
    suspend fun queryTodoListFinished(
        @Field("status") status: Int = 1,
        @Field("type") type: Long,
        @Field("priority") priority: Int,
        @Path("index") index: Int
    ): QueryTodoResponse

    /**@param index 页码*/
    @FormUrlEncoded
    @POST("lg/todo/v2/list/{index}/json")
    suspend fun queryTodoListNotFinished(
        @Field("status") status: Int = -1,
        @Field("type") type: Long,
        @Field("priority") priority: Int,
        @Path("index") index: Int
    ): QueryTodoResponse

    /**@param index 页码*/
    @FormUrlEncoded
    @POST("lg/todo/v2/list/{index}/json")
    suspend fun queryTodoListAll(
        @Field("type") type: Long,
        @Field("priority") priority: Int,
        @Path("index") index: Int,
    ): QueryTodoResponse

    companion object {
        val INSTANCE by lazy { ToDoApiGenerator.getApiService(ToDoService::class) }
    }
}