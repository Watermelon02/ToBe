package watermelon.tobe.view

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.ViewParent
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import watermelon.tobe.viewmodel.DateViewModel

/**
 * description ： TODO:类的作用
 * author : Watermelon02
 * email : 1446157077@qq.com
 * date : 2022/7/14 21:25
 */
class CollapsedRecycleView(context: Context, attrs: AttributeSet?) : RecyclerView(context, attrs) {
    private var firstInit = true
    private var collapsedHeight = 0
    private var expandedHeight = 0
    private var childRect: Rect = Rect()

    //上一个被选中的child
    private var lastChosenChild = -1
    var collapsedState = DateViewModel.CollapsedState.HALF_EXPAND
        set(value) {
            if (value != field) {
                firstInit = true
                for (i in 0..childCount) {
                    getChildAt(i)?.let {
                        (it as CollapseDayItem).collapsedState = collapsedState
                    }
                }
            }
            if (value == DateViewModel.CollapsedState.COLLAPSED) expandListener?.invoke() else if (value == DateViewModel.CollapsedState.EXPAND) collapseListener?.invoke()
            field = value
        }
    var collapseListener: (() -> Unit)? = null
    var expandListener: (() -> Unit)? = null

    override fun onMeasure(widthSpec: Int, heightSpec: Int) {
        val collapseLayout = getCollapseLayout(parent)
        if (firstInit) {
            collapsedHeight = collapseLayout.collapsedHeight
            expandedHeight = collapseLayout.expandedHeight
            firstInit = false
        }
        for (i in 0..childCount) {
            val child = getChildAt(i)
            if (child != null) {
                getChildAt(i).updateLayoutParams<MarginLayoutParams> {
                    this.bottomMargin =
                        ((MeasureSpec.getSize(heightSpec) - collapsedHeight) * 0.1).toInt()
                }
                (child as CollapseDayItem).collapsedState = collapsedState
            }
        }
        super.onMeasure(widthSpec, heightSpec)
    }

    override fun onInterceptTouchEvent(e: MotionEvent): Boolean {
        if (e.action == MotionEvent.ACTION_UP) {
            val y = e.rawY
            val x = e.rawX
            for (i in 0..childCount) {
                val child = getChildAt(i)
                if (child != null) {
                    child.getGlobalVisibleRect(childRect)
                    if (childRect.contains(x.toInt(), y.toInt())) {//如果选中了该view
                        if (i != lastChosenChild) {
                            (child as CollapseDayItem).chooseAnimate()
                            if (lastChosenChild != -1) getChildAt(lastChosenChild)?.let { (it as CollapseDayItem).cancelAnimate() }
                            lastChosenChild = i
                        }
                    }
                }
            }
        }
        return super.onInterceptTouchEvent(e)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        //切换月份时将已经选中的日期的背景还原
        getChildAt(lastChosenChild)?.let {
            (getChildAt(lastChosenChild) as CollapseDayItem).cancelAnimate()
        }
        lastChosenChild = -1
    }

    private fun getCollapseLayout(parent: ViewParent): CollapseLayout {
        return if (parent is CollapseLayout) parent else getCollapseLayout(parent.parent)
    }
}