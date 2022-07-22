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
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import watermelon.tobe.viewmodel.DateViewModel
import kotlin.math.absoluteValue

/**
 * description ： TODO:类的作用
 * author : Watermelon02
 * email : 1446157077@qq.com
 * date : 2022/7/21 19:10
 */
class CollapsedViewPagerLayout(context: Context, attrs: AttributeSet?) :
    LinearLayout(context, attrs) {
    private var collapsedHeight = 0
    private var expandedHeight = 0
    private var lastY = 0f
    private var lastX = 0f
    private var totalDy = 0f
    private var dy = 0f
    private var dx = 0f
    private var scrollDirection = -1
    var innerScroll = false
    private val collapsedParentLayout: CollapsedParentLayout by lazy {
        getCollapseParentLayout(
            parent
        )
    }
    var collapsedState = DateViewModel.CollapsedState.COLLAPSED
    private val dayFragmentVp: ViewGroup by lazy { (parent as ViewGroup).getChildAt(5) as ViewGroup }
    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        collapsedHeight = collapsedParentLayout.collapsedHeight
        expandedHeight = collapsedParentLayout.expandedHeight
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        when (ev?.action) {
            MotionEvent.ACTION_DOWN -> {
                lastY = ev.rawY
                lastX = ev.rawX
            }
            MotionEvent.ACTION_MOVE -> {
                dy = ev.rawY - lastY
                dx = ev.rawX - lastX
                if (scrollDirection == -1) {
                    scrollDirection =
                        if (dy.absoluteValue > dx.absoluteValue) VERTICAL else HORIZONTAL
                }
                if (scrollDirection == VERTICAL) {
                    if (dy.absoluteValue > dx.absoluteValue && !innerScroll) {
                        //禁止vp2横向滑动
                        (getChildAt(0) as ViewPager2).isUserInputEnabled = false
                        getInnerScrollLayout(dayFragmentVp)?.let {
                            if ((dy > 0 && it.height + dy <= expandedHeight) || (dy < 0 && it.height >= collapsedHeight)) {
                                (parent as CollapsedParentLayout).verticalScrollingListener?.invoke()
                                val childHeight = it.getChildAt(0).height
                                it.getChildAt(0).updateLayoutParams<MarginLayoutParams> {
                                    this.height = childHeight + dy.toInt()
                                    requestLayout()
                                }
                                totalDy += dy.toInt()
                            }
                        }
                    }
                } else {
                    (getChildAt(0) as ViewPager2).isUserInputEnabled = true
                }
                lastY = ev.rawY
                lastX = ev.rawX
            }
            else -> {
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
                }
                (getChildAt(0) as ViewPager2).isUserInputEnabled = true
                scrollDirection = -1
                innerScroll = false
                totalDy = 0f
                lastX = 0f
                lastY = 0f
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                lastY = ev.rawY
                lastX = ev.rawX
                return false
            }
            MotionEvent.ACTION_MOVE -> {
                lastY = ev.rawY
                lastX = ev.rawX
            }
        }
        return super.onInterceptTouchEvent(ev)
    }

    private fun generateExpandAnimator() =
        ValueAnimator.ofInt(
            getInnerScrollLayout(dayFragmentVp)!!.height,
            expandedHeight
        ).apply {
            addUpdateListener {
                getInnerScrollLayout(dayFragmentVp)?.getChildAt(0)
                    ?.updateLayoutParams<ViewGroup.LayoutParams> {
                        height = it.animatedValue as Int
                        requestLayout()
                    }
            }
            doOnEnd {
                (getInnerScrollLayout(dayFragmentVp)?.getChildAt(0) as CollapsedRecycleView).collapsedState =
                    DateViewModel.CollapsedState.EXPAND
                collapsedState = DateViewModel.CollapsedState.EXPAND
                collapsedParentLayout.expandListener?.invoke()
                scrollDirection = -1
            }
        }


    private fun generateCollapseAnimator() = ValueAnimator.ofInt(
        getInnerScrollLayout(dayFragmentVp)!!.height,
        collapsedHeight
    ).apply {
        addUpdateListener {
            getInnerScrollLayout(dayFragmentVp)?.getChildAt(0)
                ?.updateLayoutParams<ViewGroup.LayoutParams> {
                    height = it.animatedValue as Int
                    requestLayout()
                }
        }
        doOnEnd {
            (getInnerScrollLayout(dayFragmentVp)?.getChildAt(0) as CollapsedRecycleView).collapsedState =
                DateViewModel.CollapsedState.COLLAPSED
            collapsedState = DateViewModel.CollapsedState.COLLAPSED
            collapsedParentLayout.collapseListener?.invoke()
            scrollDirection = -1
        }
    }

    private fun getCollapseParentLayout(parent: ViewParent): CollapsedParentLayout {
        return if (parent is CollapsedParentLayout) parent else getCollapseParentLayout(parent.parent)
    }

    private fun getInnerScrollLayout(child: ViewGroup): InnerScrollLayout? {
        if (child is InnerScrollLayout) return child
        var innerScrollLayout: InnerScrollLayout? = null
        for (i in 0 until child.childCount) {
            val view = child.getChildAt(i)
            if (view is InnerScrollLayout) return view
            if (view is ViewGroup) innerScrollLayout = getInnerScrollLayout(view)
        }
        return innerScrollLayout
    }

    private fun getInnerRecyclerView(child: ViewGroup): RecyclerView? {
        if (child is RecyclerView) return child
        var recyclerView: RecyclerView? = null
        for (i in 0 until child.childCount) {
            val view = child.getChildAt(i)
            if (view is RecyclerView) return view
            if (view is ViewGroup) recyclerView = getInnerRecyclerView(child)
        }
        return recyclerView
    }
}