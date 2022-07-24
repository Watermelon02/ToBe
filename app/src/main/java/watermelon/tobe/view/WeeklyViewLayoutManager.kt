package watermelon.tobe.view

import android.animation.ValueAnimator
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
    reverseLayout: Boolean,
) : LinearLayoutManager(context, orientation, reverseLayout) {
    private val itemRect = SparseArray<Rect>()
    private var itemHeight = 0
    private var itemWidth = 0
    var totalDx = 0
    var exDx = 0f
    var week = 0
    private var totalWidth = 0
    private var interval = 0
    var scrollPosition = 0
    private lateinit var recycler: RecyclerView.Recycler
    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        super.onLayoutChildren(recycler, state)
        if (state.isPreLayout) return
        if (state.itemCount > 0) {
            this.recycler = recycler
            //回收复用
            detachAndScrapAttachedViews(recycler)
            //随便获取一个view,测量宽高
            val view = recycler.getViewForPosition(0)
            //7天，所以除7
            interval = width / 7
            var offsetX = 0
            itemWidth = getDecoratedMeasuredWidth(view)
            itemHeight = getDecoratedMeasuredHeight(view)
            for (i in 0 until itemCount) {//获取所有item的rect，并存入itemRect
                val rect = Rect(offsetX, 0, offsetX + itemWidth, itemHeight)
                itemRect[i] = rect
                offsetX += interval
            }
            totalWidth = (itemCount / 7) * width
            for (i in 0 until itemCount) {
                val rect = itemRect[i]
                val view = recycler.getViewForPosition(i)
                addView(view)
                measureChildWithMargins(view, 0, 0)
                layoutDecorated(view,
                    rect.left - totalDx,
                    rect.top,
                    rect.right - totalDx,
                    rect.bottom)
            }
        }
    }

    override fun onLayoutCompleted(state: RecyclerView.State?) {
        super.onLayoutCompleted(state)
    }

    override fun scrollHorizontallyBy(
        dx: Int,
        recycler: RecyclerView.Recycler,
        state: RecyclerView.State?,
    ): Int {
        var traversal = 0
        if (childCount <= 0) {
            return dx
        }
        traversal = if (totalDx + dx < -100) {
            0
        } else if (totalDx + dx > totalWidth) {
            0
        } else dx
        totalDx += traversal
        offsetChildrenHorizontal(-traversal)
        return dx
    }

    override fun onScrollStateChanged(state: Int) {
        super.onScrollStateChanged(state)
        if (state == 0) {
            if (totalDx < week * width) {
                val count = (findFirstVisibleItemPosition() ) / 7
                if (count * width >= 0) {
                    animateToX(totalDx - count * width)
                    week = count
                    totalDx = week * width
                }
            } else if (totalDx > (week) * width){
                val count = (findLastVisibleItemPosition() ) / 7
                if (count * width <= totalWidth) {
                    animateToX(totalDx - count * width)
                    week = count
                    totalDx = week * width
                }
            }
        }
    }

    private fun animateToX(aimDx:Int){
        val animator = ValueAnimator.ofInt(0,aimDx)
        var test = 0
        var lastX = 0
        animator.addUpdateListener {
            val dx = it.animatedValue as Int-lastX
            offsetChildrenHorizontal(dx)
            test+=dx
            lastX = it.animatedValue as Int
        }
        animator.duration =250
        animator.start()
    }
}