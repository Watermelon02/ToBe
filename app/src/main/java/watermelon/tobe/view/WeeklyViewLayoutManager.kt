package watermelon.tobe.view

import android.content.Context
import android.graphics.Rect
import android.util.Log
import android.util.SparseArray
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * description ： 周视图LayoutManger，用于刚好展示一周的数据
 * author : Watermelon02
 * email : 1446157077@qq.com
 * date : 2022/7/18 12:16
 */
class WeeklyViewLayoutManager(
    context: Context?,
    orientation: Int,
    reverseLayout: Boolean
) : LinearLayoutManager(context, orientation, reverseLayout) {
    private val itemRect = SparseArray<Rect>()
    private var itemHeight = 0
    private var itemWidth = 0
    private var totalDx = 0
    private var totalWidth = 0
    var scrollPosition = 0
    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        super.onLayoutChildren(recycler, state)
        if (state.isPreLayout) return
        if (state.itemCount > 0) {
            //回收复用
            detachAndScrapAttachedViews(recycler)
            //随便获取一个view,测量宽高
            val view = recycler.getViewForPosition(0)
            //7天，所以除7
            val interval = width / 7
            val visibleCount = width / interval
            var offsetX = 0
            itemWidth = getDecoratedMeasuredWidth(view)
            itemHeight = getDecoratedMeasuredHeight(view)
            for (i in 0 until itemCount) {//获取所有item的rect，并存入itemRect
                val rect = Rect(offsetX, 0, offsetX + itemWidth, itemHeight)
                itemRect[i] = rect
                offsetX += interval
            }
            totalWidth = maxOf(width,totalDx)
            for (i in 0 until visibleCount) {
                val rect = itemRect[i]
                val view = recycler.getViewForPosition(i+scrollPosition)
                addView(view)
                measureChildWithMargins(view, 0, 0)
                layoutDecorated(view, rect.left, rect.top, rect.right, rect.bottom)
            }
        }
    }
}