package watermelon.tobe.repo.bean

import watermelon.tobe.service.aidl.Todo

data class QueryTodoResponse(
    val `data`: Data,
    val errorCode: Int,
    val errorMsg: String
) {
    data class Data(
        val curPage: Int,
        val datas: List<Todo>,
        val offset: Int,
        val over: Boolean,
        val pageCount: Int,
        val size: Int,
        val total: Int
    )
}