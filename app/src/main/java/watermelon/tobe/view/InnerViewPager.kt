package watermelon.tobe.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.widget.FrameLayout
import android.widget.LinearLayout
import kotlin.math.absoluteValue

/**
 * description : 为了解决ViewPager2内部嵌套的ViewPager的滑动冲突的layout
 * author : Watermelon02
 * email : 1446157077@qq.com
 * date : 2022/6/23 15:07
 */
class InnerViewPagerLayout(context: Context, attrs: AttributeSet?) : LinearLayout(context, attrs) {
    private var lastX = 0f
    private var lastY = 0f
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        when (ev?.action) {
            MotionEvent.ACTION_DOWN -> {
                lastX = ev.rawX
                lastY = ev.rawY
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = ev.rawX - lastX
                val dy = ev.rawY - lastY
                if (dx.absoluteValue > dy.absoluteValue) {
                    if ((getChildAt(0).canScrollHorizontally(1) && dx < 0) || (getChildAt(0).canScrollHorizontally(
                            -1
                        ) && dx > 0)
                    ) {
                        parent.requestDisallowInterceptTouchEvent(true)
                    }
                } else {
                    parent.requestDisallowInterceptTouchEvent(false)
                }
                lastX = ev.rawX
                lastY = ev.rawY
            }
        }
        return super.dispatchTouchEvent(ev)
    }
}