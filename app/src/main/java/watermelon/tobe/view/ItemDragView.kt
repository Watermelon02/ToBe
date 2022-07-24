package watermelon.tobe.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.widget.LinearLayout
import androidx.core.view.marginEnd
import androidx.core.view.marginLeft
import androidx.core.view.marginStart
import androidx.recyclerview.widget.LinearLayoutManager
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
    private var lastClickTime = 0L
    private var direction = -1
    var doubleClickListener: (() -> Unit)? = null
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        maxDx = marginEnd.toFloat()
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                lastY = ev.rawY
                lastX = ev.rawX
                if (System.currentTimeMillis() - lastClickTime<200){
                    doubleClickListener?.invoke()
                }
                lastClickTime = System.currentTimeMillis()
                parent.requestDisallowInterceptTouchEvent(true)
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                val dy = ev.rawY - lastY
                val dx = ev.rawX - lastX
                if (direction == -1){
                    direction = if (dx.absoluteValue > dy.absoluteValue){
                        LinearLayoutManager.HORIZONTAL
                    }else LinearLayoutManager.VERTICAL
                }
                if (direction == LinearLayoutManager.HORIZONTAL) {
                    if ((dx > 0 && x + dx > maxDx) || (dx < 0 && x + dx -marginLeft < 0)) {
                    }else if ((dx > 0 &&  x + dx <= maxDx) || (dx < 0 &&  x + dx  >= marginLeft)) {
                        x += dx
                        totalDx += dx
                    }
                }else{
                    parent.requestDisallowInterceptTouchEvent(false)
                }
                lastY = ev.rawY
                lastX = ev.rawX
            }
            else -> {
                if (totalDx > maxDx / 2) {
                    animate().x(maxDx)
                } else animate().x(0f + marginLeft)
                direction = -1
                parent.requestDisallowInterceptTouchEvent(false)
                totalDx = 0f
            }
        }
        return super.dispatchTouchEvent(ev)
    }
}