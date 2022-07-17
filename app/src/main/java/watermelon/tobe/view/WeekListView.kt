package watermelon.tobe.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.view.marginEnd
import androidx.core.view.marginLeft
import androidx.core.view.marginStart

/**
 * description ： TODO:类的作用
 * author : Watermelon02
 * email : 1446157077@qq.com
 * date : 2022/7/17 12:26
 */
class WeekListView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    private val paint = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.FILL
        isFakeBoldText  = true
        textSize = 40f
    }
    private val textRect = Rect()
    private val weekList = listOf("日","一","二","三","四","五","六")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        paint.getTextBounds("一",0,1,textRect)
        val interval = (width-marginEnd-marginStart)/6
        var x = 0
        for (i in 0..6){
            canvas.drawText(weekList[i],x+textRect.width()/2f,height/2-textRect.height()/2f,paint)
            x+=interval
        }
    }
}