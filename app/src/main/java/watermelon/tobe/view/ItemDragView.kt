package watermelon.tobe.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.widget.LinearLayout
import androidx.core.view.marginEnd
import androidx.core.view.marginLeft
import androidx.core.view.marginStart
import com.google.android.material.card.MaterialCardView
import kotlin.math.absoluteValue

/**
 * description ： 可以拖动的item
 * author : Watermelon02
 * email : 1446157077@qq.com
 * date : 2022/7/20 18:29
 */
class ItemDragView(context: Context?, attrs: AttributeSet?) : MaterialCardView(context, attrs) {
    private var maxDx = 100f
    private var lastY = 0f
    var totalDx = 0f
    private var lastX = 0f
    var swipeListener: (() -> Unit)? = null
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        maxDx = marginEnd.toFloat()
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                lastY = ev.rawY
                lastX = ev.rawX
                parent.requestDisallowInterceptTouchEvent(true)
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                val dy = ev.rawY - lastY
                val dx = ev.rawX - lastX
                if (dx.absoluteValue > dy.absoluteValue) {
                    if ((dx > 0 && totalDx + dx <= maxDx) || (dx < 0 && totalDx + dx >= 0f + marginLeft)) {
                        x += dx
                        totalDx += dx
                        if (totalDx == maxDx) swipeListener?.invoke()
                    }
                } else parent.requestDisallowInterceptTouchEvent(false)
                lastY = ev.rawY
                lastX = ev.rawX
            }
            MotionEvent.ACTION_UP -> {
                if (totalDx > maxDx / 2) {
                    animate().x(maxDx)
                    swipeListener?.invoke()
                } else animate().x(0f + marginLeft)
                parent.requestDisallowInterceptTouchEvent(false)
                totalDx = 0f
            }
        }
        return super.dispatchTouchEvent(ev)
    }
}