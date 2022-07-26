package watermelon.tobe.view.piechart

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View

/**
 * description ： TODO:类的作用
 * author : Watermelon02
 * email : 1446157077@qq.com
 * date : 2022/7/26 14:58
 */
abstract class PieChartView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    val pieChartViewGroup by lazy { parent as PieChartViewGroup }

    abstract fun start()
}