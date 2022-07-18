package watermelon.tobe.view

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.doOnEnd
import androidx.core.view.updateLayoutParams
import androidx.viewpager2.widget.ViewPager2
import watermelon.tobe.viewmodel.DateViewModel
import kotlin.math.absoluteValue

/**
 * description ： TODO:类的作用
 * author : Watermelon02
 * email : 1446157077@qq.com
 * date : 2022/7/14 20:14
 */
class CollapseLayout(context: Context, attrs: AttributeSet?) : ConstraintLayout(context, attrs) {
    private var lastY = 0f
    private var lastX = 0f
    private var totalDy = 0f
    var isScrolling = false
    var collapsedState = DateViewModel.CollapsedState.COLLAPSED
    private var screenHeight = 0
    var collapsedHeight = 0
    var expandedHeight = 0
    var collapseListener: (() -> Unit)? = null
    var expandListener: (() -> Unit)? = null
    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (collapsedHeight == 0 && expandedHeight == 0) {
            screenHeight = measuredHeight
            collapsedHeight = (screenHeight * 0.1f).toInt()
            expandedHeight = (screenHeight * 0.9f).toInt()
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return when (ev?.action) {
            MotionEvent.ACTION_DOWN -> {
                lastY = ev.rawY
                lastX = ev.rawX
                return false
            }
            MotionEvent.ACTION_MOVE -> {
                val dy = ev.rawY - lastY
                val dx = ev.rawX - lastX
                return if (dy.absoluteValue > dx.absoluteValue) {
                    lastY = ev.rawY
                    lastX = ev.rawX
                    true
                } else {
                    lastY = ev.rawY
                    lastX = ev.rawX
                    false
                }
            }
            MotionEvent.ACTION_UP -> {
                return false
            }
            else -> return true
        }
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                lastY = event.rawY
                lastX = event.rawX
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                val dy = event.rawY - lastY
                val dx = event.rawX - lastX
                val childHeight1 = getChildAt(5).height
                if ((dy > 0 && childHeight1 + dy <= expandedHeight) || (dy < 0 && childHeight1 >= collapsedHeight)) {
                    isScrolling = true
                    getChildAt(5).updateLayoutParams<LayoutParams> {
                        this.height = childHeight1 + dy.toInt()
                        requestLayout()
                    }
                    totalDy += dy.toInt()
                }
                lastY = event.rawY
                lastX = event.rawX
            }
            else -> {
                val childHeight1 = getChildAt(5).height
                if (totalDy > (expandedHeight - collapsedHeight) * 0.5) {
                    val expandAnimator = ValueAnimator.ofInt(childHeight1, expandedHeight)
                    expandAnimator.addUpdateListener {
                        getChildAt(5).updateLayoutParams<LayoutParams> {
                            this.height = it.animatedValue as Int
                            requestLayout()
                        }
                    }
                    expandAnimator.doOnEnd {
                        collapsedState = DateViewModel.CollapsedState.EXPAND
                        getCollapsedRecyclerView()?.collapsedState =  DateViewModel.CollapsedState.EXPAND
                        expandListener?.invoke()
                        isScrolling = false
                    }
                    expandAnimator.start()
                } else {
                    val collapseAnimator = ValueAnimator.ofInt(childHeight1, collapsedHeight)
                    collapseAnimator.addUpdateListener {
                        getChildAt(5).updateLayoutParams<LayoutParams> {
                            this.height = it.animatedValue as Int
                            requestLayout()
                        }
                    }
                    collapseAnimator.doOnEnd {
                        collapsedState = DateViewModel.CollapsedState.COLLAPSED
                        getCollapsedRecyclerView()?.collapsedState =  DateViewModel.CollapsedState.COLLAPSED
                        collapseListener?.invoke()
                        isScrolling = false
                    }
                    collapseAnimator.start()
                }
            }
        }

        return super.onTouchEvent(event)
    }

    private fun getCollapsedRecyclerView(): CollapsedRecycleView? {
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child is ViewPager2) {
                val innerViewPagerLayout = (((((((child).getChildAt(0)) as ViewGroup).getChildAt(0)) as ViewGroup).getChildAt(0))as ViewGroup ).getChildAt(0)
                return innerViewPagerLayout as CollapsedRecycleView
            }
        }
        return null
    }

}