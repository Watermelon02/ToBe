package watermelon.tobe.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.ViewParent
import android.widget.LinearLayout
import androidx.core.animation.doOnEnd
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import watermelon.tobe.viewmodel.DateViewModel

/**
 * description ： 可以折叠展开的Rv,用于切换月视图和周视图，会向下的CollapseDayItem通知折叠状态的改变
 * author : Watermelon02
 * email : 1446157077@qq.com
 * date : 2022/7/14 21:25
 */
class CollapsedRecycleView(context: Context, attrs: AttributeSet?) : RecyclerView(context, attrs) {
    private var firstInit = true
    private var collapsedHeight = 0
    private var expandedHeight = 0
    private var childRect: Rect = Rect()
    var isScrolling = false
    val collapseLayout by lazy { getCollapseLayout(parent) }

    //上一个被选中的child
    private var lastChosenChild = -1
    var collapsedState = DateViewModel.CollapsedState.COLLAPSED
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
    private var lastX = 0f
    private var lastY = 0f
    private var totalDy = 0f

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