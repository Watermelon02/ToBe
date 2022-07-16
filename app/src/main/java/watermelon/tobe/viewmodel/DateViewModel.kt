package watermelon.tobe.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import watermelon.lightmusic.util.extension.toast
import watermelon.tobe.repo.repository.DateRepository

/**
 * author : Watermelon02
 * email : 1446157077@qq.com
 * date : 2022/7/14 14:32
 */
class DateViewModel : ViewModel() {

    var collapsedState = CollapsedState.COLLAPSED
    suspend fun queryHoliday(date: String) = DateRepository.queryHoliday(date).catch {
        Log.d("testTag", "(DateViewModel.kt:22) -> ${it.message}")
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        null
    )


    suspend fun queryMonth(month: String) = DateRepository.queryMonth(month).catch {
        Log.d("testTag", "(DateViewModel.kt:32) -> ${it.message}")
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        null
    )

    //上方vp的折叠状态
    enum class CollapsedState {
        EXPAND, COLLAPSED, HALF_EXPAND
    }
}