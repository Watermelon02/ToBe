package watermelon.tobe.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.ViewGroup
import androidx.core.view.marginTop
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView

/**
 * description ： TODO:类的作用
 * author : Watermelon02
 * email : 1446157077@qq.com
 * date : 2022/7/14 21:25
 */
class CollapsedRecycleView(context: Context, attrs: AttributeSet?) : RecyclerView(context, attrs) {
    private var firstInit = true
    private var originalHeight = 0
    override fun onMeasure(widthSpec: Int, heightSpec: Int) {
        if (firstInit) {
            originalHeight = MeasureSpec.getSize(heightSpec)
            firstInit = false
        }
        for (i in 0..childCount){
            getChildAt(i)?.updateLayoutParams<MarginLayoutParams> {
                this.bottomMargin = ((MeasureSpec.getSize(heightSpec)-originalHeight)*0.1).toInt()
                this.topMargin = ((MeasureSpec.getSize(heightSpec)-originalHeight)*0.1).toInt()
            }
        }
        super.onMeasure(widthSpec, heightSpec)
    }

    
}