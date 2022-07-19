package watermelon.tobe.ui.activity

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.viewpager2.widget.ViewPager2
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import watermelon.lightmusic.base.BaseActivity
import watermelon.tobe.R
import watermelon.tobe.databinding.ActivityDateBinding
import watermelon.tobe.repo.bean.Todo
import watermelon.tobe.repo.service.ToDoService
import watermelon.tobe.ui.adapter.DayInfoAdapter
import watermelon.tobe.ui.adapter.MonthAdapter
import watermelon.tobe.util.extension.safeLaunch
import watermelon.tobe.util.local.DateCalculator
import watermelon.tobe.util.local.DateCalculator.TOTAL_MONTH
import watermelon.tobe.util.local.DateCalculator.lastDay
import watermelon.tobe.util.local.DateCalculator.lastMonth
import watermelon.tobe.util.local.DateCalculator.lastYear
import watermelon.tobe.util.network.ToDoApiGenerator
import watermelon.tobe.viewmodel.DateViewModel
import java.util.*

class DateActivity : BaseActivity() {
    private lateinit var viewModel: DateViewModel
    private var isScrolling = false
    private var firstInit = false

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[DateViewModel::class.java]
        val binding = DataBindingUtil.setContentView<ActivityDateBinding>(
            this,
            R.layout.activity_date
        )
        binding.apply {
            activityDateViewPagerMonth.adapter =
                MonthAdapter(this@DateActivity, TOTAL_MONTH)
            //旋转180度，让其向左排列月份；同时在MonthFragment中让里面的内容翻转180度,以正常显示内容
            activityDateViewPagerMonth.rotationY = 180f
            activityDateViewPagerMonth.currentItem = TOTAL_MONTH / 2
            activityDateViewPagerDay.adapter =
                DayInfoAdapter(this@DateActivity, DateCalculator.getDays(0), viewModel)
            activityDateCollapseLayout.collapseListener = {
                viewModel.emitCollapsedState(DateViewModel.CollapsedState.COLLAPSED)
            }
            activityDateCollapseLayout.expandListener = {
                viewModel.emitCollapsedState(DateViewModel.CollapsedState.EXPAND)
            }
            safeLaunch {//初始化,让上方vp到中间当前月份
                //初始化下方vp的数据
                viewModel.emitDays("${viewModel.currentYear}-${viewModel.currentMonth}")
            }
            //对当前vp应该显示日期的监听，当接收到数据时，切换到对应item
            safeLaunch {
                DateCalculator.currentDate.collect {
                    val currentYear = it.split("-")[0].toInt()
                    val currentMonth = it.split("-")[1].toInt()
                    val currentDay = it.split("-")[2].toInt()
                    if (currentYear != lastYear || currentMonth != lastMonth) {
                        //当切换了月份后点击日期,需要改变下方vp中的值
                        viewModel.emitDays("${currentYear}-${currentMonth}")
                    }
                    //day-1才是对应的index
                    activityDateViewPagerDay.currentItem = currentDay - 1
                    lastYear = currentYear
                    lastMonth = currentMonth
                    lastDay = currentDay
                }
            }
            //对当前选中月份对应的日期List的监听，当值改变时，经过DiffUtil比对后对下方的Vp进行差分刷新
            safeLaunch {
                viewModel.dayFragmentDays.collectLatest {
                    if (it.size > 1) {//在初始化flow的时候会发送一个listOf("")，需要排除这种情况
                        val diffResult = DiffUtil.calculateDiff(
                            DayInfoAdapter.NoteDiffUtil(
                                (activityDateViewPagerDay.adapter as DayInfoAdapter).days,
                                it
                            )
                        )
                        diffResult.dispatchUpdatesTo(activityDateViewPagerDay.adapter as DayInfoAdapter)
                        (activityDateViewPagerDay.adapter as DayInfoAdapter).days = it
                    }
                }
            }
            //监听上方月份VP的滑动，根据滑动切换activityDateViewPagerMonth的值
            activityDateViewPagerMonth.registerOnPageChangeCallback(object :
                ViewPager2.OnPageChangeCallback() {
                private var lastPosition = TOTAL_MONTH / 2
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
                    //改变年份
                    val calendar = Calendar.getInstance()
                    calendar.add(Calendar.MONTH, TOTAL_MONTH / 2 - position)
                    val yearTextWidth = binding.activityDateNumberYear.width
                    if (calendar[Calendar.YEAR] < viewModel.currentYear) {
                        binding.activityDateNumberYear.animate().x(yearTextWidth / 2f).alpha(0f)
                            .withEndAction {
                                binding.activityDateNumberYear.text =
                                    calendar[Calendar.YEAR].toString()
                                activityDateNumberYear.x = -yearTextWidth / 2f
                                activityDateNumberYear.animate().x(0f).alpha(1f)
                            }
                    } else if (calendar[Calendar.YEAR] > viewModel.currentYear) {
                        binding.activityDateNumberYear.animate().x(-yearTextWidth / 2f).alpha(0f)
                            .withEndAction {
                                binding.activityDateNumberYear.text =
                                    calendar[Calendar.YEAR].toString()
                                activityDateNumberYear.x = yearTextWidth / 2f
                                activityDateNumberYear.animate().x(0f).alpha(1f)
                            }
                    }
                    viewModel.currentYear = calendar[Calendar.YEAR]
                    viewModel.currentMonth = calendar[Calendar.MONTH]
                    //改变月份
                    if (position > lastPosition) {
                        activityDateNumberMonth.changePosition(1)
                    } else {
                        activityDateNumberMonth.changePosition(0)
                    }
                    lastPosition = position
                }
            })
        }
        safeLaunch {
            //test
            lifecycleScope.launch (Dispatchers.IO){
                ToDoService.INSTANCE.login("1446157077@qq.com","ai1wei2xi3")
                val response = ToDoService.INSTANCE.queryTodoList(0,0,0,1)
                Log.d("testTag", "(DateActivity.kt:150) -> ${response.data.datas[0].content}")
            }
        }
    }
}