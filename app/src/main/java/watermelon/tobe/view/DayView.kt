package watermelon.tobe.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.widget.TextView
import androidx.core.animation.doOnEnd

/**
 * description ： TODO:类的作用
 * author : Watermelon02
 * email : 1446157077@qq.com
 * date : 2022/7/18 10:21
 */
class DayView(context: Context, attrs: AttributeSet) :
    androidx.appcompat.widget.AppCompatTextView(context, attrs) {
    private val paint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.FILL
        strokeWidth = 2f
        isAntiAlias = true
    }
    private var animator: ValueAnimator? = null
    private var beChosen = false

    //被选中时的背景圆圈半径
    private var backGroundRadius = 0f

    override fun onDraw(canvas: Canvas) {
        canvas.drawRoundRect(left.toFloat(), 0f, right.toFloat(), backGroundRadius, 10f, 10f, paint)
        if (beChosen) setTextColor(Color.BLACK) else setTextColor(Color.BLACK)
        super.onDraw(canvas)
    }

    fun chooseAnimate() {
        beChosen = true
        animator = ValueAnimator.ofFloat(0f, 1f)
        animator?.addUpdateListener {
            val value = it.animatedValue as Float
            backGroundRadius = value * height / 2
            invalidate()
        }
        animator?.start()
    }

    fun cancelAnimate() {
        //取消正在播放的动画
        animator?.cancel()
        animator = ValueAnimator.ofFloat(backGroundRadius * 2 / height, 0f)
        animator?.addUpdateListener {
            val value = it.animatedValue as Float
            backGroundRadius = value * height / 2
            invalidate()
        }
        animator?.doOnEnd {
            beChosen = false
        }
        animator?.start()
    }
}