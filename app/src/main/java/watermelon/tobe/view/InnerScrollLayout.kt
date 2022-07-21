package watermelon.tobe.view

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.ViewParent
import android.widget.LinearLayout
import androidx.core.animation.doOnEnd
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import watermelon.tobe.viewmodel.DateViewModel
import kotlin.math.absoluteValue

/**
 * description : 为了解决ViewPager2内部嵌套的ViewPager类似滑动冲突的layout
 * author : Watermelon02
 * email : 1446157077@qq.com
 * date : 2022/6/23 15:07
 */
class InnerScrollLayout(context: Context, attrs: AttributeSet?) : LinearLayout(context, attrs) {
    private var lastX = 0f
    private var lastY = 0f
    private var totalDy = 0f
    private var totalDx = 0f
    private var collapsedHeight = 0
    var expandedHeight = 0
    private var firstInit = false
    private var isScrolling = false
    private var scrollDirection = -1
    var collapsedState = DateViewModel.CollapsedState.COLLAPSED
        set(value) {
            if (value != field) {
                getChildAt(0)?.let {
                    (it as CollapsedRecycleView).collapsedState = collapsedState
                }
            }
            field = value
        }
    val collapsedParentLayout: CollapsedParentLayout by lazy {
        getCollapseParentLayout(
            parent
        )
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        if (!firstInit) {
            collapsedHeight = collapsedParentLayout.collapsedHeight
            expandedHeight = collapsedParentLayout.expandedHeight
            firstInit = true
        }
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
                if (scrollDirection == -1) {
                    scrollDirection =
                        if (dx.absoluteValue > dy.absoluteValue) HORIZONTAL else VERTICAL
                }
                if (scrollDirection == HORIZONTAL) {
                    (getChildAt(0) as CollapsedRecycleView).layoutManager?.let {
                        if (it is LinearLayoutManager) {
                            if ((it.findFirstVisibleItemPosition() == 0 && dx > 0) ||
                                (it.findLastVisibleItemPosition() == 34 && dx < 0) || (it.findLastVisibleItemPosition() == 41 && dx < 0)
                            ) {
                                parent.requestDisallowInterceptTouchEvent(false)
                            } else {
                                parent.requestDisallowInterceptTouchEvent(true)
                            }
                        }
                    }
                    totalDx += dx.toInt()
                } else {//垂直滑动
                    if ((dy > 0 && height + dy <= expandedHeight) || (dy < 0 && height >= collapsedHeight)) {
                        collapsedParentLayout.verticalScrollingListener?.invoke()
                        val childHeight = getChildAt(0).height
                        parent.requestDisallowInterceptTouchEvent(true)
                        getChildAt(0).updateLayoutParams<MarginLayoutParams> {
                            this.height = childHeight + dy.toInt()
                            requestLayout()
                        }
                        isScrolling = true
                        totalDy += dy.toInt()
                    }
                }
                lastY = ev.rawY
                lastX = ev.rawX
            }
            MotionEvent.ACTION_UP -> {
                if (scrollDirection == VERTICAL) {
                    if (totalDy > 0) {//展开中
                        if (totalDy > (expandedHeight - collapsedHeight) * 0.5) {
                            generateExpandAnimator().start()
                        } else {
                            generateCollapseAnimator().start()
                        }
                    } else {//收缩中
                        if (totalDy.absoluteValue < (expandedHeight - collapsedHeight) * 0.5) {
                            generateExpandAnimator().start()
                        } else {
                            generateCollapseAnimator().start()
                        }
                    }
                } else if (scrollDirection == HORIZONTAL) {
                    if (collapsedState == DateViewModel.CollapsedState.COLLAPSED) {
                        val rv = getChildAt(0) as RecyclerView
                        if (rv.layoutManager is WeeklyViewLayoutManager) {
                            val count =
                                if (totalDx < 0) {
                                    (rv.layoutManager as WeeklyViewLayoutManager).findLastVisibleItemPosition() / 7
                                } else {
                                    (rv.layoutManager as WeeklyViewLayoutManager).findFirstVisibleItemPosition() / 7
                                }
                            (rv.layoutManager as WeeklyViewLayoutManager).scrollPosition = count * 7
                            (rv.layoutManager as WeeklyViewLayoutManager).totalDx = 0
                            rv.layoutManager?.requestLayout()
                        }
                    }
                }
                scrollDirection = -1
                totalDy = 0f
                totalDx = 0f
                lastX = 0f
                lastY = 0f
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun generateExpandAnimator() =
        ValueAnimator.ofInt(height, expandedHeight).apply {
            addUpdateListener {
                getChildAt(0).updateLayoutParams<ViewGroup.LayoutParams> {
                    this.height = it.animatedValue as Int
                    requestLayout()
                }
            }
            doOnEnd {
                collapsedState = DateViewModel.CollapsedState.EXPAND
                (getChildAt(0) as CollapsedRecycleView).collapsedState =
                    DateViewModel.CollapsedState.EXPAND
                collapsedParentLayout.expandListener?.invoke()
                isScrolling = false
            }
        }


    private fun generateCollapseAnimator() = ValueAnimator.ofInt(height, collapsedHeight).apply {
        addUpdateListener {
            getChildAt(0).updateLayoutParams<ViewGroup.LayoutParams> {
                this.height = it.animatedValue as Int
                requestLayout()
            }
        }
        doOnEnd {
            collapsedState = DateViewModel.CollapsedState.COLLAPSED
            (getChildAt(0) as CollapsedRecycleView).collapsedState =
                DateViewModel.CollapsedState.COLLAPSED
            collapsedParentLayout.collapseListener?.invoke()
            isScrolling = false
        }
    }

    private fun getCollapseParentLayout(parent: ViewParent): CollapsedParentLayout {
        return if (parent is CollapsedParentLayout) parent else getCollapseParentLayout(parent.parent)
    }
}