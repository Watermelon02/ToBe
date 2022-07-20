package watermelon.tobe.view

import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * description ： 重写GridLayoutManager，以禁止内部滑动功能
 * author : Watermelon02
 * email : 1446157077@qq.com
 * date : 2022/7/14 20:55
 */
class MonthlyViewLayoutManager(context: Context?, spanCount: Int) :
    GridLayoutManager(context, spanCount) {
    override fun canScrollVertically(): Boolean = false
}