package watermelon.tobe.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.ViewParent
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.LinearLayout.VERTICAL
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import watermelon.tobe.ui.adapter.TodoAdapter
import kotlin.math.absoluteValue

/**
 * description ： 解决嵌套滑动的布局
 * author : Watermelon02
 * email : 1446157077@qq.com
 * date : 2022/7/22 09:42
 */
class InnerRecyclerView(context: Context, attrs: AttributeSet?) : LinearLayout(context, attrs) {
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
                if (scrollDirection == VERTICAL) {
                    if (dy.absoluteValue > dx.absoluteValue) {
                        val scrollToBottom =
                            ((getChildAt(0) as RecyclerView).adapter as TodoAdapter).todoList.size-1 ==
                                    ((getChildAt(0) as RecyclerView).layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
                        val scrollToTop =
                            0 == ((getChildAt(0) as RecyclerView).layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
                        if ((!scrollToBottom && dy < 0) || (!scrollToTop && dy > 0)
                        ) {
                            parent.requestDisallowInterceptTouchEvent(true)
                            getCollapseViewPagerLayout(parent).innerScroll = true
                        } else if (scrollToBottom && scrollToTop) {//如果rv能展示所有数据，则不处理滑动事件
                            parent.requestDisallowInterceptTouchEvent(false)
                            getCollapseViewPagerLayout(parent).innerScroll = false
                        } else {
                            parent.requestDisallowInterceptTouchEvent(false)
                            getCollapseViewPagerLayout(parent).innerScroll = false
                        }
                    } else {
                        parent.requestDisallowInterceptTouchEvent(false)
                        getCollapseViewPagerLayout(parent).innerScroll = false
                    }
                }
                lastX = ev.rawX
                lastY = ev.rawY
            }
            else -> {
                parent.requestDisallowInterceptTouchEvent(false)
                scrollDirection = -1
                getCollapseViewPagerLayout(parent).apply {
                    innerScroll = false
                    if (height > 0) {//展开中
                        if (getInnerScrollLayout(dayFragmentVp)!!.getChildAt(0).height > (expandedHeight - collapsedHeight) * 0.5) {
                            generateCollapseAnimator().start()
                        } else {
                            generateExpandAnimator().start()
                        }
                    } else {//收缩中
                        if (height < (expandedHeight - collapsedHeight) * 0.5) {
                            generateCollapseAnimator().start()
                        } else {
                            generateExpandAnimator().start()
                        }
                    }
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        if (ev?.action == MotionEvent.ACTION_DOWN) return false
        return super.onInterceptTouchEvent(ev)
    }

    private fun getCollapseViewPagerLayout(parent: ViewParent): CollapsedViewPagerLayout {
        return if (parent is CollapsedViewPagerLayout) parent else getCollapseViewPagerLayout(parent.parent)
    }
}