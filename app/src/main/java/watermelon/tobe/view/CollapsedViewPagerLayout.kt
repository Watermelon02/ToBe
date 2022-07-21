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
    private val collapsedParentLayout: CollapsedParentLayout by lazy {
        getCollapseParentLayout(
            parent
        )
    }
    var collapsedState = DateViewModel.CollapsedState.COLLAPSED
    private val dayFragmentVp :ViewGroup by lazy { (parent as ViewGroup).getChildAt(5) as ViewGroup }
    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        collapsedHeight = collapsedParentLayout.collapsedHeight
        expandedHeight = collapsedParentLayout.expandedHeight
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                lastY = ev.rawY
                lastX = ev.rawX
            }
            MotionEvent.ACTION_MOVE -> {
                val dy = ev.rawY - lastY
                getInnerScrollLayout(dayFragmentVp)?.let {
                    if ((dy > 0 && it.height + dy <= expandedHeight) || (dy < 0 && it.height >= collapsedHeight)) {
                        (parent as CollapsedParentLayout).verticalScrollingListener?.invoke()
                        val childHeight = it.getChildAt(0).height
                        it.getChildAt(0).updateLayoutParams<MarginLayoutParams> {
                            Log.d("testTag", "(CollapsedViewPagerLayout.kt:57) -> ${dy.toInt()}")
                            this.height = childHeight + dy.toInt()
                            requestLayout()
                        }
                        totalDy += dy.toInt()
                    }
                }
                lastY = ev.rawY
                lastX = ev.rawX
            }
            MotionEvent.ACTION_UP -> {
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
                totalDy = 0f
                lastX = 0f
                lastY = 0f
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun generateExpandAnimator() =
        ValueAnimator.ofInt(
            getInnerScrollLayout(dayFragmentVp)!!.height,
            expandedHeight
        ).apply {
            addUpdateListener {
                getInnerScrollLayout(dayFragmentVp)?.getChildAt(0)?.updateLayoutParams<ViewGroup.LayoutParams> {
                    height = it.animatedValue as Int
                    requestLayout()
                }
            }
            doOnEnd {
                (getInnerScrollLayout(dayFragmentVp)?.getChildAt(0) as CollapsedRecycleView).collapsedState =
                    DateViewModel.CollapsedState.EXPAND
                collapsedState = DateViewModel.CollapsedState.EXPAND
                collapsedParentLayout.expandListener?.invoke()
            }
        }


    private fun generateCollapseAnimator() = ValueAnimator.ofInt(
        getInnerScrollLayout(dayFragmentVp)!!.height,
        collapsedHeight
    ).apply {
        addUpdateListener {
            getInnerScrollLayout(dayFragmentVp)?.getChildAt(0)?.updateLayoutParams<ViewGroup.LayoutParams> {
                height = it.animatedValue as Int
                requestLayout()
            }
        }
        doOnEnd {
            (getInnerScrollLayout(dayFragmentVp)?.getChildAt(0) as CollapsedRecycleView).collapsedState =
                DateViewModel.CollapsedState.COLLAPSED
            collapsedState = DateViewModel.CollapsedState.COLLAPSED
            collapsedParentLayout.collapseListener?.invoke()
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
}