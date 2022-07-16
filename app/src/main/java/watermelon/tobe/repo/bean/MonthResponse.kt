package watermelon.tobe.repo.bean

data class MonthResponse(
    val code: Int,
    val `data`: List<Day>,
    val msg: String
)