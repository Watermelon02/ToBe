package watermelon.tobe.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import watermelon.tobe.viewmodel.DateViewModel
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
    var collapsedState = DateViewModel.CollapsedState.HALF_EXPAND
        set(value) {
            if (value != field) {
                    getChildAt(0)?.let {
                        (it as CollapsedRecycleView).collapsedState = collapsedState
                    }
            }
            field = value
        }
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
                    (getChildAt(0) as CollapsedRecycleView).layoutManager?.let {
                        Log.d("testTag", "(InnerViewPager.kt:33) -> ${(it as GridLayoutManager).findFirstVisibleItemPosition()}.${dx}")
                        if (((it as GridLayoutManager).findFirstVisibleItemPosition() == 0 && dx > 0) ||
                            ((it as GridLayoutManager).findLastVisibleItemPosition() == 34 && dx < 0)||((it as GridLayoutManager).findLastVisibleItemPosition() == 41 && dx < 0)
                        ) {
                            Log.d("testTag", "(InnerViewPager.kt:33) -> 1")
                            parent.requestDisallowInterceptTouchEvent(false)
                        } else {
                            Log.d("testTag", "(InnerViewPager.kt:36) -> 2")
                            parent.requestDisallowInterceptTouchEvent(true)
                        }
                    }
                } else {
                }
                lastX = ev.rawX
                lastY = ev.rawY
            }
        }
        return super.dispatchTouchEvent(ev)
    }
}