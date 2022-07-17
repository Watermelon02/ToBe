package watermelon.tobe.view

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.doOnEnd
import androidx.core.view.updateLayoutParams
import watermelon.tobe.viewmodel.DateViewModel
import kotlin.math.absoluteValue

/**
 * description ： TODO:类的作用
 * author : Watermelon02
 * email : 1446157077@qq.com
 * date : 2022/7/14 20:14
 */
class CollapseLayout(context: Context, attrs: AttributeSet?) : ConstraintLayout(context, attrs) {
    private var maxDy = 0f
    private var minDy = 0f
    private var lastY = 0f
    private var lastX = 0f
    private var totalDy = 0f
    var isPulling = false
    var collapsedState = DateViewModel.CollapsedState.COLLAPSED
    var collapsedHeight = 0
    var expandedHeight = 0
    var collapseListener: (() -> Unit)? = null
    var expandListener: (() -> Unit)? = null
    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return when (ev?.action) {
            MotionEvent.ACTION_DOWN -> {
                lastY = ev.rawY
                lastX = ev.rawX
                maxDy = height * 0.8f
                minDy = height * 0.1f
                return false
            }
            MotionEvent.ACTION_MOVE -> {
                val dy = ev.rawY - lastY
                val dx = ev.rawX - lastX
                return if (dy.absoluteValue > dx.absoluteValue) {
                    lastY = ev.rawY
                    lastX = ev.rawX
                    true
                } else {
                    lastY = ev.rawY
                    lastX = ev.rawX
                    false
                }
            }
            MotionEvent.ACTION_UP -> {
                return false
            }
            else -> return true
        }
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                lastY = event.rawY
                lastX = event.rawX
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                val dy = event.rawY - lastY
                val dx = event.rawX - lastX
                val childHeight1 = getChildAt(4).height
                if ((dy > 0 && childHeight1 + dy <= maxDy) || (dy < 0 && childHeight1 >= minDy)) {
                    isPulling = true
                    getChildAt(4).updateLayoutParams<LayoutParams> {
                        this.height = childHeight1 + dy.toInt()
                        requestLayout()
                    }
                    totalDy += dy.toInt()
                }
                lastY = event.rawY
                lastX = event.rawX
            }
            else -> {
                val childHeight1 = getChildAt(4).height

                if (totalDy > (maxDy - minDy) * 0.5) {
                    val expandAnimator = ValueAnimator.ofInt(childHeight1, maxDy.toInt())
                    expandAnimator.addUpdateListener {
                        getChildAt(4).updateLayoutParams<LayoutParams> {
                            this.height = it.animatedValue as Int
                            requestLayout()
                        }
                    }
                    expandAnimator.doOnEnd {
                        collapsedState = DateViewModel.CollapsedState.EXPAND
                        expandListener?.invoke()
                        isPulling = false
                    }
                    expandAnimator.start()
                } else {
                    val collapseAnimator = ValueAnimator.ofInt(childHeight1, minDy.toInt())
                    collapseAnimator.addUpdateListener {
                        getChildAt(4).updateLayoutParams<LayoutParams> {
                            this.height = it.animatedValue as Int
                            requestLayout()
                        }
                    }
                    collapseAnimator.doOnEnd {
                        collapsedState = DateViewModel.CollapsedState.COLLAPSED
                        collapseListener?.invoke()
                        isPulling = false
                    }
                    collapseAnimator.start()
                }
            }
        }

        return super.onTouchEvent(event)
    }


}