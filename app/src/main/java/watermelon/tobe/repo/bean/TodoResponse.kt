package watermelon.tobe.repo.bean

import androidx.room.PrimaryKey

data class TodoResponse(
    val data: Todo,
    val errorCode: Int,
    val errorMsg: String
)