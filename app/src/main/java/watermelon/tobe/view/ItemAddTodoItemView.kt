package watermelon.tobe.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import com.google.android.material.card.MaterialCardView

/**
 * description ： 用于解决AddTodoFragment中的时间选择vp的滑动冲突
 * author : Watermelon02
 * email : 1446157077@qq.com
 * date : 2022/7/21 11:23
 */
class ItemAddTodoItemView(context: Context?, attrs: AttributeSet?) :
    MaterialCardView(context, attrs) {
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        when(ev.action){
            MotionEvent.ACTION_DOWN->parent.requestDisallowInterceptTouchEvent(true)
            MotionEvent.ACTION_UP->parent.requestDisallowInterceptTouchEvent(false)
        }
        return super.dispatchTouchEvent(ev)
    }
}