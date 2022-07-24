package watermelon.tobe.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import watermelon.tobe.util.local.DateCalculator
import java.util.*

/**
 * description ： DateActivity上方可以滚动的显示月份View
 * author : Watermelon02
 * email : 1446157077@qq.com
 * date : 2022/7/15 09:14
 */
class ScrollableMonthView(context: Context, attrs: AttributeSet?) :
    androidx.appcompat.widget.AppCompatTextView(
        context, attrs
    ) {
    private val paint = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.FILL
        strokeWidth = 2f
        isAntiAlias = true
    }
    private val rect = Rect()
    private var translateX = 0f

    private var monthList = arrayListOf(
        Month(Calendar.getInstance()[Calendar.MONTH]),
        Month(Calendar.getInstance()[Calendar.MONTH] + 1),
        Month(Calendar.getInstance()[Calendar.MONTH] + 2)
    )
    private var interval = 0
    private var direction = 0
    private var position = 0

    override fun onDraw(canvas: Canvas) {
        paint.textSize = 60f
        paint.getTextBounds(10.toString(), 0, 10.toString().length, rect)
        val centerX = width / 2 - rect.width() / 2
        interval = rect.width() * 4
        val translate = if (direction == 0) {
            -translateX % interval
        } else {
            interval - translateX % interval
        }
        monthList[1].x = centerX - translate
        monthList[0].x = centerX - translate - interval
        monthList[2].x = centerX - translate + interval
        canvas.drawText(
            monthList[0].month.toString(),
            monthList[0].x,
            (rect.height()).toFloat(),
            paint
        )
        canvas.drawText(
            monthList[1].month.toString(),
            monthList[1].x,
            (rect.height()).toFloat(),
            paint
        )
        canvas.drawText(
            monthList[2].month.toString(),
            monthList[2].x,
            (rect.height()).toFloat(),
            paint
        )
        super.onDraw(canvas)
    }

    //当activity上方的vp左右滑动完后，根据滑动的方向重新设置三个month的月份和位置
    //当向右滑动完成时，将1，2号month存储到2,3号位中，3号存储到1号位中并--,如果--后 == 0，则将其变为12（意味着变成了上一年的12月）;
    // 向左时逻辑相反
    fun changePosition(direction: Int) {
        if (direction == 0) {
            val tempX = monthList[1].x
            val tempMonth = monthList[1].month
            monthList[1].x = monthList[2].x
            monthList[1].month = monthList[2].month
            monthList[0].x = tempX
            monthList[0].month = tempMonth
            monthList[2].month++
            monthList[2].x = (monthList[1].x + interval)
            if (monthList[2].month == 13) monthList[2].month = 1
        }
        if (direction == 1) {
            val tempX = monthList[0].x
            val tempMonth = monthList[0].month
            monthList[2].x = monthList[1].x
            monthList[2].month = monthList[1].month
            monthList[1].x = tempX
            monthList[1].month = tempMonth
            monthList[0].month--
            monthList[0].x = monthList[1].x - interval
            if (monthList[0].month == 0) monthList[0].month = 12
            for (i in 0..2) {
                monthList[i].x += 2 * interval
            }
        }
        invalidate()
    }

    //平移位置
    fun translate(direction: Int, offset: Float) {
        this.direction = direction
        translateX = (4f * rect.width()) * (position + offset)
        invalidate()
    }

    fun resetToCurrentMonth(){
        animate().alpha(0f).withEndAction {
            monthList = arrayListOf(
                Month(Calendar.getInstance()[Calendar.MONTH]),
                Month(Calendar.getInstance()[Calendar.MONTH] + 1),
                Month(Calendar.getInstance()[Calendar.MONTH] + 2)
            )
            animate().alpha(1f)
        }
    }

    inner class Month(var month: Int = 0, var x: Float = 0f)
}