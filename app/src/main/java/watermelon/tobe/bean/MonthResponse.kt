package watermelon.tobe.bean

data class MonthResponse(
    val code: Int,
    val `data`: List<Day>,
    val msg: String
)