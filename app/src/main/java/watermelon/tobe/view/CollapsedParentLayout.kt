package watermelon.tobe.view

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import watermelon.tobe.viewmodel.DateViewModel

/**
 * description ： 可以展开折叠的布局，用于产生折叠和展开的事件，并通过接口向ViewModel分发，通过遍历向下分发来完成切换周月视图的功能
 * author : Watermelon02
 * email : 1446157077@qq.com
 * date : 2022/7/14 20:14
 */
class CollapsedParentLayout(context: Context, attrs: AttributeSet?) : ConstraintLayout(context, attrs) {
    var collapsedState = DateViewModel.CollapsedState.COLLAPSED
    var screenHeight = 0
    var collapsedHeight = 0
    var expandedHeight = 0
    var nestedChildHeight = 0
    var collapseListener: (() -> Unit)? = null
    var expandListener: (() -> Unit)? = null
    var verticalScrollingListener: (() -> Unit)? = null

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (collapsedHeight == 0 && expandedHeight == 0) {
            screenHeight = measuredHeight
            collapsedHeight = (screenHeight * 0.08f).toInt()
            expandedHeight = (screenHeight * 0.6f).toInt()
            nestedChildHeight = 0
            for (i in 0..4) {
                nestedChildHeight += getChildAt(i).measuredHeight
            }
        }
    }
}