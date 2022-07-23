package watermelon.tobe.repo.bean

import androidx.room.PrimaryKey
import watermelon.tobe.service.aidl.Todo

data class TodoResponse(
    val data: Todo,
    val errorCode: Int,
    val errorMsg: String
)