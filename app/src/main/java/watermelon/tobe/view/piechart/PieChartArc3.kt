package watermelon.tobe.view.piechart

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.view.View
import androidx.core.graphics.alpha

/**
 * description ： TODO:类的作用
 * author : Watermelon02
 * email : 1446157077@qq.com
 * date : 2022/7/26 14:38
 */
class PieChartArc3(context: Context?, attrs: AttributeSet?) : PieChartView(context, attrs) {
    private var yellowAlpha1 = 0
    private var yellowAlpha2 = 0
    private var yellowColor1 = Color.WHITE
    private var yellowColor2 = Color.WHITE
    private val finishPaint = Paint().apply {
        color = Color.BLUE
        style = Paint.Style.FILL
    }
    private val circlePaint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.FILL
    }
    private var yellowAnimator1 = ValueAnimator.ofFloat(1f, 0.2f).apply {
        duration = 6000
        repeatMode = ValueAnimator.REVERSE
        repeatCount = ValueAnimator.INFINITE
    }
    private var yellowAnimator2: ValueAnimator = ValueAnimator.ofFloat(1f, 5f).apply {
        duration = 8000
        repeatMode = ValueAnimator.REVERSE
        repeatCount = ValueAnimator.INFINITE
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val radius = width / 2f
        finishPaint.shader = LinearGradient(
            0f,
            0f,
            width.toFloat(),
            height.toFloat(),
            arrayOf(yellowColor1, yellowColor2).toIntArray(),
            null,
            Shader.TileMode.REPEAT
        )
        canvas.drawArc(0f, 0f, width.toFloat(), width.toFloat(), 240f, 120f, true, finishPaint)
        canvas.drawCircle(width / 2f, height / 2f, radius - 15, circlePaint)

    }

    override fun start() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            yellowAlpha1 = (Color.GREEN.alpha * 0.8).toInt()
            yellowAlpha2 = (Color.GREEN.alpha * 0.2).toInt()
            yellowAnimator1.addUpdateListener {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    this.yellowColor1 = Color.argb(
                        (yellowAlpha1 * (it.animatedValue as Float)).toInt(),
                        Color.red(Color.parseColor("#FFFFFF")),
                        Color.green(PieChartViewGroup.GREEN),
                        Color.blue(PieChartViewGroup.GREEN)
                    )
                }
            }
            yellowAnimator2.addUpdateListener {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    this.yellowColor2 = Color.argb(
                        (yellowAlpha2 * (it.animatedValue as Float)).toInt(),
                        Color.red(PieChartViewGroup.GREEN),
                        Color.green(PieChartViewGroup.GREEN),
                        Color.blue(PieChartViewGroup.GREEN)
                    )
                }
            }
            yellowAnimator1.start()
            yellowAnimator2.start()
        }
    }
}