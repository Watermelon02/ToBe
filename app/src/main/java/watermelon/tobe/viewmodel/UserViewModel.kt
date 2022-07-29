package watermelon.tobe.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import watermelon.tobe.base.BaseApp
import watermelon.tobe.repo.repository.TodoRepository
import watermelon.tobe.repo.repository.UserRepository
import watermelon.tobe.repo.service.ToDoService

/**
 * author : Watermelon02
 * email : 1446157077@qq.com
 * date : 2022/7/22 16:16
 */
class UserViewModel : ViewModel() {
    val user by lazy { UserRepository.user }
    private val _isShowing = MutableStateFlow(false)
    val isShowing = _isShowing.asStateFlow()
    private val _finishPercent = MutableStateFlow(0f)
    val finishPercent = _finishPercent.asStateFlow()
    fun login(userName: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            UserRepository.login(userName, password)
            BaseApp.todoManagerBinder?.login()
        }
    }

    fun exit() {
        viewModelScope.launch(Dispatchers.IO) {
            ToDoService.INSTANCE.exit()
            user.emit(null)
            BaseApp.todoManagerBinder?.exit()
            BaseApp.appContext.getSharedPreferences(UserRepository.KEY_USER, Context.MODE_PRIVATE).edit()
                .putString(UserRepository.KEY_USERNAME, "").putString(UserRepository.KEY_PASSWORD, "").apply()
        }
    }

    fun register(userName: String, password: String, rePassword: String) {
        viewModelScope.launch(Dispatchers.IO) {
            UserRepository.register(userName, password, rePassword)
        }
    }

    fun emitShowState(boolean: Boolean) {
        viewModelScope.launch {
            _isShowing.emit(boolean)
        }
    }

    fun queryTodoFinishState(){
        viewModelScope.launch(Dispatchers.IO) {
            _finishPercent.emit(TodoRepository.getFinishPercent())
        }
    }
}