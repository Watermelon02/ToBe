package watermelon.tobe.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
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
    var collapsedState = DateViewModel.CollapsedState.COLLAPSED
        set(value) {
            if (value != field) {
                firstInit = true
                for (i in 0.. childCount){
                    getChildAt(i)?.let {
                        (it as CollapseDayItem).collapsedState = collapsedState
                    }
                }
            }
            field = value
        }

    override fun onMeasure(widthSpec: Int, heightSpec: Int) {
        if (collapsedState == DateViewModel.CollapsedState.COLLAPSED) {
            if (firstInit) {
                collapsedHeight = MeasureSpec.getSize(heightSpec)
                getCollapseLayout(parent).collapsedHeight = collapsedHeight
                firstInit = false
            }
            for (i in 0..childCount) {
                val child = getChildAt(i)
                if (child!=null){
                    getChildAt(i).updateLayoutParams<MarginLayoutParams> {
                        this.bottomMargin =
                            ((MeasureSpec.getSize(heightSpec) - collapsedHeight) * 0.1).toInt()
                        this.topMargin =
                            ((MeasureSpec.getSize(heightSpec) - collapsedHeight) * 0.1).toInt()
                    }
                    (child as CollapseDayItem).collapsedState = collapsedState
                }

            }
        } else {
            if (firstInit) {
                expandedHeight = MeasureSpec.getSize(heightSpec)
                getCollapseLayout(parent).expandedHeight = expandedHeight
                firstInit = false
            }
            for (i in 0..childCount) {
                val child = getChildAt(i)
                if (child!=null) {
                    child.updateLayoutParams<MarginLayoutParams> {
                        this.bottomMargin =
                            ((MeasureSpec.getSize(heightSpec) - collapsedHeight) * 0.1).toInt()
                        this.topMargin =
                            ((MeasureSpec.getSize(heightSpec) - collapsedHeight) * 0.1).toInt()
                    }
                    (child as CollapseDayItem).collapsedState = collapsedState
                }
            }
        }
        super.onMeasure(widthSpec, heightSpec)
    }

    fun getCollapseLayout(parent:ViewParent): CollapseLayout {
        return if (parent is CollapseLayout) parent else getCollapseLayout(parent.parent)
    }
}