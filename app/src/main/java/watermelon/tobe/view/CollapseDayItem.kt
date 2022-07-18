package watermelon.tobe.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewParent
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.marginBottom
import androidx.core.view.updateLayoutParams
import watermelon.tobe.viewmodel.DateViewModel

/**
 * description ： 可以点击的日期布局
 * author : Watermelon02
 * email : 1446157077@qq.com
 * date : 2022/7/16 21:52
 */
class CollapseDayItem(context: Context, attrs: AttributeSet?) : LinearLayout(context, attrs) {
    var collapsedState = DateViewModel.CollapsedState.COLLAPSED

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val collapseLayout = getCollapseLayout(parent)
        if (!collapseLayout.isScrolling && collapseLayout.collapsedState == DateViewModel.CollapsedState.EXPAND) {
            updateLayoutParams<MarginLayoutParams> {
                this.bottomMargin =
                    ((collapseLayout.expandedHeight - collapseLayout.collapsedHeight) * 0.1).toInt()
                requestLayout()
            }
        }
    }

    private fun getCollapseLayout(parent: ViewParent): CollapseLayout {
        return if (parent is CollapseLayout) parent else getCollapseLayout(parent.parent)
    }

    //  被选中时的动画
    fun chooseAnimate() {
        (getChildAt(0) as DayView).chooseAnimate()
    }

    //取消正在播放的动画
    fun cancelAnimate() {
        (getChildAt(0) as DayView).cancelAnimate()
    }
}