package watermelon.tobe.view.piechart

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout

/**
 * description ： 统计完成和未完成Todo的饼状图
 * author : Watermelon02
 * email : 1446157077@qq.com
 * date : 2022/7/26 09:41
 */
class PieChartViewGroup(context: Context, attrs: AttributeSet?) :
    FrameLayout(context, attrs) {
    companion object {
        val RED = Color.parseColor("#FE4365")
        val YELLOW = Color.parseColor("#F9CDAD")
        val GREEN = Color.parseColor("#83AF9B")
    }
    private val textPaint = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.FILL
        textSize = 20f
    }
    private var touchState = TouchState.NULL
    private var centerText = "TEST"

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {

            }
        }
        return super.onTouchEvent(event)
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val radius = width / 2f
        //空心圆
        canvas.drawText(centerText,
            width / 2f - textPaint.measureText(centerText) / 2,
            height / 2f + textPaint.textSize / 2,
            textPaint)
    }

    fun start() {
            scaleY = 1.3f
            scaleX =1.3f
            animate().scaleY(1f).scaleX(1f)
            for (i in 0..2){
                if (getChildAt(i) is PieChartView) (getChildAt(i) as PieChartView).start()
                val animator = ValueAnimator.ofFloat(0f,360f)
                animator.addUpdateListener {
                    rotation = it.animatedValue as Float
                }
                animator.repeatMode = ValueAnimator.RESTART
                animator.repeatCount = ValueAnimator.INFINITE
                animator.duration = 18000
                animator.start()
            }
    }

    enum class TouchState {
        FINISHED, NOT_FINISHED, OUT_OF_DATE, NULL
    }
}