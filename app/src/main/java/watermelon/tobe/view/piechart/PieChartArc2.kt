package watermelon.tobe.view.piechart

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
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
class PieChartArc2(context: Context?, attrs: AttributeSet?) : PieChartView(context, attrs) {
    private var blueAlpha1 = 0
    private var blueAlpha2 = 0
    private var blueColor1 = Color.WHITE
    private var blueColor2 = Color.WHITE
    private val circlePaint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.FILL
    }
    private val notFinishedPaint = Paint().apply {
        color = Color.GREEN
        style = Paint.Style.FILL
    }
    private var blueAnimator1 = ValueAnimator.ofFloat(1f, 0.2f).apply {
        duration = 5000
        repeatMode = ValueAnimator.REVERSE
        repeatCount = ValueAnimator.INFINITE
    }
    private var blueAnimator2: ValueAnimator = ValueAnimator.ofFloat(1f, 5f).apply {
        duration = 3000
        repeatMode = ValueAnimator.REVERSE
        repeatCount = ValueAnimator.INFINITE
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        Log.d("testTag", "(PieChartView.kt:17) -> 2")
        return super.onTouchEvent(event)
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val radius = width / 2f
        notFinishedPaint.shader = LinearGradient(
            0f,
            0f,
            width.toFloat(),
            height.toFloat(),
            arrayOf(blueColor1, blueColor2).toIntArray(),
            null,
            Shader.TileMode.REPEAT
        )
        canvas.drawArc(0f,
            0f,
            width.toFloat(),
            width.toFloat(),
            120f,
            120f,
            true,
            notFinishedPaint)
        canvas.drawCircle(width / 2f, height / 2f, radius - 15, circlePaint)

    }

    override fun start() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            blueAlpha1 = (Color.BLUE.alpha * 0.8).toInt()
            blueAlpha2 = (Color.BLUE.alpha * 0.2).toInt()
        }
        blueAnimator1.addUpdateListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                this.blueColor1 = Color.argb(
                    (blueAlpha1 * (it.animatedValue as Float)).toInt(),
                    Color.red(PieChartViewGroup.YELLOW),
                    Color.green(PieChartViewGroup.YELLOW),
                    Color.blue(PieChartViewGroup.YELLOW)
                )
            }
        }
        blueAnimator2.addUpdateListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                this.blueColor2 = Color.argb(
                    (blueAlpha2 * (it.animatedValue as Float)).toInt(),
                    Color.red(PieChartViewGroup.YELLOW),
                    Color.green(PieChartViewGroup.YELLOW),
                    Color.blue(PieChartViewGroup.YELLOW)
                )
            }
        }
        blueAnimator1.start()
        blueAnimator2.start()
    }
}