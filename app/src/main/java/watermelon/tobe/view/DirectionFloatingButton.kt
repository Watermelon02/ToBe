package watermelon.tobe.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Matrix
import android.util.AttributeSet
import com.google.android.material.floatingactionbutton.FloatingActionButton
import watermelon.tobe.R
import watermelon.tobe.viewmodel.DateViewModel

/**
 * author : Watermelon02
 * email : 1446157077@qq.com
 * date : 2022/7/20 20:54
 */
class DirectionFloatingButton(context: Context, attrs: AttributeSet?) :
    FloatingActionButton(context, attrs) {
    var lastCollapseState = DateViewModel.CollapsedState.HALF_EXPAND
    private val duration = 150L

    fun collapsedStateAnimate(collapsedState: DateViewModel.CollapsedState) {
        if (collapsedState == DateViewModel.CollapsedState.SCROLLING) {
            if (lastCollapseState == DateViewModel.CollapsedState.COLLAPSED) {
                animate().alpha(0.1f).setDuration(duration).withEndAction {
                    setImageResource(R.drawable.ic_arrow_bottom)
                    animate().setDuration(duration).alpha(1f)
                }
            } else if (lastCollapseState == DateViewModel.CollapsedState.EXPAND) {
                animate().alpha(0.1f).setDuration(duration).withEndAction {
                    setImageResource(R.drawable.ic_arrow_top)
                    animate().setDuration(duration).alpha(1f)
                }
            }
        }
        if (lastCollapseState == DateViewModel.CollapsedState.SCROLLING && lastCollapseState != collapsedState) {
            animate().alpha(0.1f).setDuration(duration).withEndAction {
                setImageResource(R.drawable.ic_add)
                animate().setDuration(duration).alpha(1f)
            }
        }
        lastCollapseState = collapsedState
    }

    fun addTodoAnimate() {
        animate().alpha(0f).withEndAction {
            setImageResource(R.drawable.ic_loading)
            animate().alpha(1f)
        }
    }

    fun cancelAddTodoAnimate(){
        animate().alpha(0f).withEndAction {
            setImageResource(R.drawable.ic_add)
            animate().alpha(1f)
        }
    }
}