package watermelon.tobe.view.piechart

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.shapes.ArcShape
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.graphics.alpha

/**
 * description ： TODO:类的作用
 * author : Watermelon02
 * email : 1446157077@qq.com
 * date : 2022/7/26 14:38
 */
class PieChartArc1(context: Context?, attrs: AttributeSet?) : PieChartView(context, attrs) {
    private var redAlpha1 = 0
    private var redAlpha2 = 0
    private var redColor1 = Color.WHITE
    private var redColor2 = Color.WHITE
    private val outOfDatePaint = Paint().apply {
        color = PieChartViewGroup.RED
        style = Paint.Style.FILL
    }
    private var redAnimator1 = ValueAnimator.ofFloat(1f, 0.25f).apply {
        duration = 9000
        repeatMode = ValueAnimator.REVERSE
        repeatCount = ValueAnimator.INFINITE
    }
    private var redAnimator2: ValueAnimator = ValueAnimator.ofFloat(1f, 4f).apply {
        duration = 4000
        repeatMode = ValueAnimator.REVERSE
        repeatCount = ValueAnimator.INFINITE
    }
    private val circlePaint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.FILL
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        Log.d("testTag", "(PieChartView.kt:17) -> 1")
        return super.onTouchEvent(event)
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val radius = width / 2f
        outOfDatePaint.shader = LinearGradient(
            0f,
            0f,
            width.toFloat(),
            height.toFloat(),
            arrayOf(redColor1, redColor2).toIntArray(),
            null,
            Shader.TileMode.REPEAT
        )
        canvas.drawArc(0f,
            0f,
            width.toFloat(),
            width.toFloat(),
            0f,
            120f,
            true,
            outOfDatePaint)
        canvas.drawCircle(width / 2f, height / 2f, radius - 15, circlePaint)

    }

    override fun start() {
        redAnimator1.removeAllUpdateListeners()
        redAnimator2.removeAllUpdateListeners()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            redAlpha1 = (PieChartViewGroup.RED.alpha * 0.8).toInt()
            redAlpha2 = (PieChartViewGroup.RED.alpha * 0.2).toInt()
        }
        redAnimator2.addUpdateListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                this.redColor2 = Color.argb(
                    (redAlpha2 * (it.animatedValue as Float)).toInt(),
                    Color.red(PieChartViewGroup.RED),
                    Color.green(PieChartViewGroup.RED),
                    Color.blue(PieChartViewGroup.RED)
                )
            }
        }
        redAnimator1.addUpdateListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                this.redColor1 = Color.argb(
                    (redAlpha1 * (it.animatedValue as Float)).toInt(),
                    Color.red(PieChartViewGroup.RED),
                    Color.green(PieChartViewGroup.RED),
                    Color.blue(PieChartViewGroup.RED)
                )
                invalidate()
            }
        }
        redAnimator1.start()
        redAnimator2.start()
    }
}