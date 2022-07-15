package watermelon.tobe.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import watermelon.tobe.database.DayDatabase

/**
 * author : Watermelon02
 * email : 1446157077@qq.com
 * date : 2022/7/14 14:32
 */
class DateViewModel  : ViewModel() {
    private val dayDatabase by lazy { DayDatabase.getInstance() }

    fun queryDays(time: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                /*Days.postValue(DayDatabase.getDayDao().queryDays(time) as List<Day>?)*/
            }
        }
    }

    fun queryDay(time: String) = dayDatabase.getDayDao().queryDay(time)
}