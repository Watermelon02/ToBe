package watermelon.tobe.view

import android.content.Context
import android.graphics.drawable.Animatable
import android.util.AttributeSet
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
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
    private var loadingAnimator: AnimatedVectorDrawableCompat? = null

    fun collapsedStateAnimate(collapsedState: DateViewModel.CollapsedState) {
        if (collapsedState == DateViewModel.CollapsedState.SCROLLING) {
            if (lastCollapseState == DateViewModel.CollapsedState.COLLAPSED) {
                animate().alpha(0.1f).setDuration(duration).withEndAction {
                    rotation = 0f
                    setImageResource(R.drawable.ic_arrow_bottom)
                    animate().setDuration(duration).alpha(1f)
                }
            } else if (lastCollapseState == DateViewModel.CollapsedState.EXPAND) {
                animate().alpha(0.1f).setDuration(duration).withEndAction {
                    rotation = 0f
                    setImageResource(R.drawable.ic_arrow_top)
                    animate().setDuration(duration).alpha(1f)
                }
            }
        }
        if (lastCollapseState == DateViewModel.CollapsedState.SCROLLING && lastCollapseState != collapsedState) {
            animate().alpha(0.1f).setDuration(duration).withEndAction {
                rotation = 90f
                setImageResource(R.drawable.ic_menu)
                animate().setDuration(duration).alpha(1f)
            }
        }
        lastCollapseState = collapsedState
    }

    fun addTodoAnimate(context: Context) {
        loadingAnimator = AnimatedVectorDrawableCompat.create(context, R.drawable.animated_vector_menu)
        this.setImageDrawable(loadingAnimator)
        (this.drawable as Animatable).start()
    }

    fun cancelAddTodoAnimate(){
        loadingAnimator?.stop()
        setImageResource(R.drawable.ic_menu)
    }
}