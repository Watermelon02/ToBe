package watermelon.tobe.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import watermelon.tobe.repo.repository.UserRepository
import watermelon.tobe.repo.service.ToDoService

/**
 * author : Watermelon02
 * email : 1446157077@qq.com
 * date : 2022/7/22 16:16
 */
class UserViewModel : ViewModel() {
    val user by lazy { UserRepository.user }
    val isShowing = MutableStateFlow(false)
    fun login(userName: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            UserRepository.login(userName, password)
        }
    }

    fun exit() {
        viewModelScope.launch(Dispatchers.IO) {
            ToDoService.INSTANCE.exit()
            user.emit(null)
        }
    }

    fun register(userName: String, password: String, rePassword: String) {
        viewModelScope.launch(Dispatchers.IO) {
            UserRepository.register(userName, password, rePassword)
        }
    }

    fun emitShowState(boolean: Boolean) {
        viewModelScope.launch {
            isShowing.emit(boolean)
        }
    }
}