package watermelon.tobe.repo.bean

import androidx.room.PrimaryKey

data class AddTodoResponse(
    val data: Todo,
    val errorCode: Int,
    val errorMsg: String
)