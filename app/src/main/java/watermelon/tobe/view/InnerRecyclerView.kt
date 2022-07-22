package watermelon.tobe.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.ViewParent
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.LinearLayout.VERTICAL
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.absoluteValue

/**
 * description ： TODO:类的作用
 * author : Watermelon02
 * email : 1446157077@qq.com
 * date : 2022/7/22 09:42
 */
class InnerRecyclerView(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {
    private var lastX = 0f
    private var lastY = 0f
    private var interceptX = 0f
    private var interceptY = 0f
    private var scrollDirection = -1

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        when (ev?.action) {
            MotionEvent.ACTION_DOWN -> {
                lastX = ev.rawX
                lastY = ev.rawY
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = ev.rawX - lastX
                val dy = ev.rawY - lastY
                if (scrollDirection == -1) {
                    scrollDirection =
                        if (dy.absoluteValue > dx.absoluteValue) LinearLayout.VERTICAL else LinearLayout.HORIZONTAL
                }
                if (scrollDirection == VERTICAL){
                    if (dy.absoluteValue > dx.absoluteValue) {
                        if ((getChildAt(0).canScrollVertically(1) && dy < 0) || (getChildAt(0).canScrollVertically(-1) && dy > 0)
                        ) {
                            parent.requestDisallowInterceptTouchEvent(true)
                            getCollapseViewPagerLayout(parent).innerScroll = true
                        }else {
                            parent.requestDisallowInterceptTouchEvent(false)
                            getCollapseViewPagerLayout(parent).innerScroll = false
                        }
                    }else{
                        parent.requestDisallowInterceptTouchEvent(false)
                        getCollapseViewPagerLayout(parent).innerScroll = false
                    }
                }
                lastX = ev.rawX
                lastY = ev.rawY
            }
            else->{
                parent.requestDisallowInterceptTouchEvent(false)
                getCollapseViewPagerLayout(parent).innerScroll = false
                scrollDirection = -1
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun getCollapseViewPagerLayout(parent: ViewParent): CollapsedViewPagerLayout {
        return if (parent is CollapsedViewPagerLayout) parent else getCollapseViewPagerLayout(parent.parent)
    }
}