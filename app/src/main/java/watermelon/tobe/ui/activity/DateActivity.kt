package watermelon.tobe.ui.activity

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.viewpager2.widget.ViewPager2
import kotlinx.coroutines.delay
import watermelon.lightmusic.base.BaseActivity
import watermelon.tobe.R
import watermelon.tobe.databinding.ActivityDateBinding
import watermelon.tobe.ui.adapter.DayInfoAdapter
import watermelon.tobe.ui.adapter.MonthAdapter
import watermelon.tobe.util.extension.safeLaunch
import watermelon.tobe.util.local.DateCalculator
import watermelon.tobe.util.local.DateCalculator.TOTAL_MONTH
import watermelon.tobe.util.local.DateCalculator.lastDay
import watermelon.tobe.util.local.DateCalculator.lastMonth
import watermelon.tobe.util.local.DateCalculator.lastYear
import watermelon.tobe.viewmodel.DateViewModel
import java.util.*

class DateActivity : BaseActivity() {
    lateinit var viewModel: DateViewModel
var isScrolling = false


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = DateViewModel()
        val dateBinding = DataBindingUtil.setContentView<ActivityDateBinding>(
            this,
            R.layout.activity_date
        )
        dateBinding.apply {
            activityDateNumberMonth.setNumber(DateCalculator.calendar2[Calendar.MONTH] + 1)
            activityDateViewPagerMonth.adapter = MonthAdapter(this@DateActivity, TOTAL_MONTH)
            //旋转180度，让其向左排列月份；同时在MonthFragment中让里面的内容翻转180度,以正常显示内容
            activityDateViewPagerMonth.rotationY = 180f
            activityDateViewPagerMonth.currentItem = TOTAL_MONTH / 2
            activityDateViewPagerDay.adapter =
                DayInfoAdapter(this@DateActivity, DateCalculator.getDays(0))
            //对当前vp应该显示日期的监听，当接收到数据时，切换到对应item
            safeLaunch {
                DateCalculator.viewPagerDayCurrentItem.collect {
                    val year = it.split("-")[0].toInt()
                    val month = it.split("-")[1].toInt()
                    val day = it.split("-")[2].toInt()
                    if (year != lastYear || month != lastMonth) {
                        //当切换了月份后点击日期
                        DateCalculator.getMonthDays(DateCalculator.calculateDiffMonth(year, month))
                    }
                    //day-1才是对应的index
                    activityDateViewPagerDay.currentItem = day - 1
                    lastYear = year
                    lastMonth = month
                    lastDay = day
                }
            }
            //对当前选中月份对应的日期List的监听，当值改变时，经过DiffUtil比对后对下方的Vp进行差分刷新
            safeLaunch {
                DateCalculator.days.collect {
                    if (it.size > 1) {//在初始化flow的时候会发送一个listOf("")，需要排除这种情况
                        val diffResult = DiffUtil.calculateDiff(
                            DayInfoAdapter.NoteDiffUtil(
                                (activityDateViewPagerDay.adapter as DayInfoAdapter).days,
                                it
                            )
                        )
                        diffResult.dispatchUpdatesTo(activityDateViewPagerDay.adapter as DayInfoAdapter)
                        (activityDateViewPagerDay.adapter as DayInfoAdapter).days = it
                        activityDateViewPagerDay.currentItem =
                            DateCalculator.viewPagerDayCurrentItem.value.split("-")[2].toInt()
                    }
                }
            }
            //监听上方月份VP的滑动，根据滑动切换activityDateViewPagerMonth的值
            activityDateViewPagerMonth.registerOnPageChangeCallback(object :
                ViewPager2.OnPageChangeCallback() {
                private var lastPosition = 0
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                    //1为RightToLeft,0为LeftToRight
                    val direction = if (lastPosition == position) 0 else 1
                    activityDateNumberMonth.translate(
                        direction,
                        positionOffset
                    )
                }

                override fun onPageScrollStateChanged(state: Int) {
                    if (state == 2) isScrolling = false
                    super.onPageScrollStateChanged(state)
                }

                override fun onPageSelected(position: Int) {
                    if (position > lastPosition) {
                        activityDateNumberMonth.stop(1)
                    } else {
                        activityDateNumberMonth.stop(0)
                    }
                    lastPosition = position
                }
            })
            //奇怪的bug的临时解决方案，待修
            activityDateViewPagerMonth.currentItem = TOTAL_MONTH / 2 -1
            activityDateViewPagerMonth.currentItem = TOTAL_MONTH / 2
        }
    }
}