package watermelon.tobe.bean

data class HolidayResponse(
    val code: Int,
    val holiday: Holiday,
    val type: Type
) {
    data class Holiday(
        val after: Boolean,
        val holiday: Boolean,
        val name: String,
        val target: String,
        val wage: Int
    )

    data class Type(
        val name: String,
        val type: Int,
        val week: Int
    )
}
enum class HolidayType(value:Int){
    WEEKDAY(0),WEEKEND(1),FESTIVAL(2),CompensatoryLeave(3)
}