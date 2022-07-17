package watermelon.tobe.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewParent
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.animation.doOnEnd
import androidx.core.view.updateLayoutParams
import androidx.palette.graphics.Palette
import watermelon.tobe.R
import watermelon.tobe.viewmodel.DateViewModel

/**
 * description ： 可以点击的日期布局
 * author : Watermelon02
 * email : 1446157077@qq.com
 * date : 2022/7/16 21:52
 */
class CollapseDayItem(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {
    private val paint = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.FILL
        strokeWidth = 2f
        isAntiAlias = true
    }
    private var childColor = 0
    private var animator: ValueAnimator? = null
    private var beChosen = false
    //被选中时的背景圆圈半径
    private var backGroundRadius = 0f
    var collapsedState = DateViewModel.CollapsedState.COLLAPSED
    var margin = 0

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val collapseLayout = getCollapseLayout(parent)
        if (!collapseLayout.isScrolling && collapseLayout.collapsedState == DateViewModel.CollapsedState.EXPAND) {
            updateLayoutParams<MarginLayoutParams> {
                margin = collapseLayout.expandedHeight - collapseLayout.collapsedHeight
                this.bottomMargin =
                    (margin * 0.1).toInt()
                this.topMargin =
                    (margin * 0.1).toInt()
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun drawChild(canvas: Canvas, child: View, drawingTime: Long): Boolean {
        canvas.drawCircle(width/2f-12,height/2f,backGroundRadius,paint)
        if (beChosen) (child as TextView).setTextColor(Color.WHITE) else (child as TextView).setTextColor(Color.BLACK)
        return super.drawChild(canvas, child, drawingTime)
    }

    private fun getCollapseLayout(parent: ViewParent): CollapseLayout {
        return if (parent is CollapseLayout) parent else getCollapseLayout(parent.parent)
    }

    //  被选中时的动画
    fun chooseAnimate() {
        beChosen = true
        animator = ValueAnimator.ofFloat(0f, 1f)
        animator?.addUpdateListener {
            val value = it.animatedValue as Float
            backGroundRadius = value * height / 2
            invalidate()
        }
        animator?.start()
    }

    fun notChooseAnimate() {
        //取消正在播放的动画
        animator?.cancel()
        animator = ValueAnimator.ofFloat(backGroundRadius * 2 / height, 0f)
        animator?.addUpdateListener {
            val value = it.animatedValue as Float
            backGroundRadius = value * height / 2
            invalidate()
        }
        animator?.doOnEnd {
            beChosen = false
        }
        animator?.start()
    }
}