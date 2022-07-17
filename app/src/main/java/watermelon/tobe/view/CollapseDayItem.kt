package watermelon.tobe.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.ViewParent
import android.widget.FrameLayout
import androidx.core.view.updateLayoutParams
import watermelon.tobe.viewmodel.DateViewModel

/**
 * description ： 可以点击的日期布局
 * author : Watermelon02
 * email : 1446157077@qq.com
 * date : 2022/7/16 21:52
 */
class CollapseDayItem(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {
    var collapsedState = DateViewModel.CollapsedState.COLLAPSED
    var margin = 0
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val collapseLayout= getCollapseLayout(parent)
        if (!collapseLayout.isPulling && collapseLayout.collapsedState == DateViewModel.CollapsedState.EXPAND){
            updateLayoutParams<MarginLayoutParams> {
                margin = getCollapseLayout(parent).expandedHeight - getCollapseLayout(parent).collapsedHeight
                this.bottomMargin =
                    (margin * 0.1).toInt()
                this.topMargin =
                    (margin * 0.1).toInt()
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }
    private fun getCollapseLayout(parent:ViewParent): CollapseLayout {
        return if (parent is CollapseLayout) parent else getCollapseLayout(parent.parent)
    }
}