package watermelon.tobe.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.ViewParent
import android.widget.LinearLayout
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
    set(value) {
        if (value != field) firstInit =true
        field = value
    }
    private var firstInit =true

    private fun getCollapseLayout(parent: ViewParent): CollapsedRecycleView {
        return if (parent is CollapsedRecycleView) parent else getCollapseLayout(parent.parent)
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