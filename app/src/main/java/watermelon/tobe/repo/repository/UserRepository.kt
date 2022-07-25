package watermelon.tobe.repo.repository

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import watermelon.tobe.util.extension.toast
import watermelon.tobe.base.BaseApp
import watermelon.tobe.repo.bean.LoginResponse
import watermelon.tobe.repo.service.ToDoService

/**
 * author : Watermelon02
 * email : 1446157077@qq.com
 * date : 2022/7/22 18:09
 */
object UserRepository {
    internal const val KEY_USER = "user"
    internal const val KEY_USERNAME = "username"
    internal const val KEY_PASSWORD = "password"
    val user = MutableStateFlow<LoginResponse?>(null)
    suspend fun tryLogin(): Boolean {
        val sp = BaseApp.appContext.getSharedPreferences(KEY_USER, Context.MODE_PRIVATE)
        val username = sp.getString(KEY_USERNAME, "")
        val password = sp.getString(KEY_PASSWORD, "")
        if (username != "" && password != "") {
            user.emit(ToDoService.INSTANCE.login(username!!, password!!))
            return true
        }
        return false
    }

    suspend fun login(userName: String, password: String) {
        return try {
            val response = ToDoService.INSTANCE.login(userName, password)
            if (response.errorCode == 0) {
                BaseApp.appContext.getSharedPreferences(KEY_USER, Context.MODE_PRIVATE).edit()
                    .putString(KEY_USERNAME, userName).putString(KEY_PASSWORD, password).apply()
            }
            user.emit(response)
        } catch (e: Exception) {//网络连接失败
            toast("网络连接失败，无法登录")
            e.printStackTrace()
            user.emit(null)
        }
    }

    suspend fun register(userName: String, password: String, rePassword: String) {
        return try {
            val response = ToDoService.INSTANCE.register(userName, password, rePassword)
            if (response.errorCode == 0) {
                BaseApp.appContext.getSharedPreferences(KEY_USER, Context.MODE_PRIVATE).edit()
                    .putString(KEY_USERNAME, userName).putString(KEY_PASSWORD, password).apply()
            }
            user.emit(response)
        } catch (e: Exception) {//网络连接失败
            toast("网络连接失败，无法注册")
            e.printStackTrace()
            user.emit(null)
        }
    }
}