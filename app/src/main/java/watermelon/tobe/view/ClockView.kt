package watermelon.tobe.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import kotlin.math.absoluteValue

/**
 * description ： T odo界面用于选择小时的View
 * author : Watermelon02
 * email : 1446157077@qq.com
 * date : 2022/7/23 10:39
 */
class ClockView(context: Context?, attrs: AttributeSet) : View(context, attrs) {
    private val clockPaint = Paint()
    private val pointerPaint = Paint()
    private val textPaint = Paint().apply {
        color = Color.WHITE
        textSize = 30f
        isAntiAlias = true
    }
    private val redPaint= Paint().apply {
        color = Color.RED
        strokeWidth = 3f
        isAntiAlias = true
    }
    private val backgroundPaint=Paint().apply {
        color = Color.parseColor("#F8F8F8")
        strokeWidth = 3f
        isAntiAlias = true
    }
    private var radius = 0
    private var bigPointer = 0
    private var smallPointer = 0
    private var lastX = 0f
    private var lastY = 0f
    private var degree = 0f
    var totalDegree = 0f
    private val mMatrix = Matrix()//用于滑动旋转
    var scrollListener: ((Float) -> Unit)? = null//滑动监听接口
    var releaseListener: ((Float) -> Unit)? = null//松开时，回调该接口

    init {
        clockPaint.color = Color.BLACK
        clockPaint.isAntiAlias = true
        pointerPaint.color = Color.BLACK
        pointerPaint.isAntiAlias = true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        radius = MeasureSpec.getSize(widthMeasureSpec)
        bigPointer = radius / 80
        smallPointer = radius / 160
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawRoundRect(width.toFloat() / 2-height/2f-10,0f,width.toFloat() / 2+height/2f+10,
            height.toFloat(),40f,40f,backgroundPaint)
        canvas.drawCircle(width.toFloat() / 2, height.toFloat() / 2, height/2f, clockPaint)
        pointerPaint.strokeWidth = 4f
        for (i in 0 until 12) {
            /*canvas.drawLine(
                width / 2f,
                height / 10f - 3 * bigPointer,
                width / 2f,
                height / 10f - (1.5 * bigPointer).toInt(),
                pointerPaint
            )*/
            if (i == 0){
                canvas.drawText(
                    "${12}",
                    width / 2f - 15f,
                    height / 10f - (1.5 * bigPointer).toInt() + 40f,
                    textPaint
                )
            }else if (i < 9)
                canvas.drawText(
                    "${i}",
                    width / 2f -10f,
                    height / 10f - (1.5 * bigPointer).toInt() + 40f,
                    textPaint
                )
            else canvas.drawText(
                "${i}",
                width / 2f - 15f,
                height / 10f - (1.5 * bigPointer).toInt() + 40f,
                textPaint
            )
            canvas.rotate(30f, width / 2f, height / 2f)
        }
        pointerPaint.strokeWidth = 2f
        /*for (i in 0 until 60) {
            if (i % 5 != 0) {
                canvas.drawLine(
                    width / 2f,
                    height / 10f - 6 * smallPointer,
                    width / 2f,
                    height / 10f - 4 * smallPointer,
                    pointerPaint
                )
            }
            canvas.rotate(6f, width / 2f, height / 2f)
        }*/
        canvas.rotate(totalDegree, width / 2f, height / 2f)
        //画红色时针，指向当前选择的小时
        canvas.drawLine(width / 2f,
            height / 10f - 3 * bigPointer,
            width / 2f,
            height / 2f,
            redPaint)
        canvas.rotate(totalDegree, width / 2f, height / 2f)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                lastX = event.rawX
                lastY = event.rawY
                parent.requestDisallowInterceptTouchEvent(true)
            }
            MotionEvent.ACTION_MOVE -> {
                degree = calculateDegree(event.rawX, event.rawY)
                mMatrix.postRotate(degree, width / 2f, height / 2f)
                invalidate()
                lastX = event.rawX
                lastY = event.rawY
                scrollListener?.invoke(totalDegree)//回调滑动监听
            }
            else -> {
                parent.requestDisallowInterceptTouchEvent(false)
                release()
            }
        }
        return true
    }

    private fun release() {
        if (totalDegree>0){
            if ((totalDegree  % 30) < 15) {
                degree = totalDegree % 30
                totalDegree -= degree
                mMatrix.postRotate(degree, width / 2f, height/2f)
                invalidate()
            } else {
                degree = (30 - totalDegree % 30)
                totalDegree += degree
                mMatrix.postRotate(degree, width / 2f, height/2f)
                invalidate()
            }
        }else{
            if ((totalDegree  % 30).absoluteValue < 15) {
                degree = totalDegree % 30
                totalDegree -= degree
                mMatrix.postRotate(degree, width / 2f, height/2f)
                invalidate()
            } else {
                degree = (30 + (totalDegree % 30))
                totalDegree -= degree
                mMatrix.postRotate(degree, width / 2f, height/2f)
                invalidate()
            }
        }
        releaseListener?.invoke(totalDegree)
    }

    private fun calculateDegree(currentX: Float, currentY: Float): Float {
        val degree = (lastX - currentX) / 4
        totalDegree += degree
        if (totalDegree >= 360f) totalDegree -=360f
        if (totalDegree <= -360f) totalDegree +=360f
        return degree
    }

}