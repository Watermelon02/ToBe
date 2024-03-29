package watermelon.tobe.ui.activity

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.viewpager2.widget.ViewPager2
import kotlinx.coroutines.flow.collectLatest
import watermelon.tobe.base.BaseActivity
import watermelon.tobe.R
import watermelon.tobe.base.BaseApp
import watermelon.tobe.base.FRAGMENT_ADD_TODO
import watermelon.tobe.base.FRAGMENT_LOGIN
import watermelon.tobe.base.FRAGMENT_USER
import watermelon.tobe.databinding.ActivityDateBinding
import watermelon.tobe.repo.repository.UserRepository
import watermelon.tobe.service.TodoManagerService
//import watermelon.tobe.service.TodoManagerService
import watermelon.tobe.service.aidl.TodoManager
import watermelon.tobe.ui.fragment.AddTodoDialogFragment
import watermelon.tobe.ui.fragment.UserFragment
import watermelon.tobe.ui.fragment.UpdateDialogFragment
import watermelon.tobe.ui.adapter.DayInfoAdapter
import watermelon.tobe.ui.adapter.MonthAdapter
import watermelon.tobe.ui.fragment.LoginFragment
import watermelon.tobe.util.extension.safeLaunch
import watermelon.tobe.util.local.DateCalculator
import watermelon.tobe.util.local.DateCalculator.TOTAL_MONTH
import watermelon.tobe.util.local.DateCalculator.lastDay
import watermelon.tobe.util.local.DateCalculator.lastMonth
import watermelon.tobe.util.local.DateCalculator.lastYear
import watermelon.tobe.util.local.DateCalculator.todayDate
import watermelon.tobe.viewmodel.AddTodoFragmentViewModel
import watermelon.tobe.viewmodel.DateViewModel
import watermelon.tobe.viewmodel.UserViewModel
import java.util.*

class DateActivity : BaseActivity() {
    private lateinit var dateViewModel: DateViewModel
    private var lastMonthVpPosition = TOTAL_MONTH / 2
    lateinit var binding: ActivityDateBinding
    private var yearTextWidth = 0
    private var isMenuExpand = false
    private val addTodoBottomSheet by lazy {
        AddTodoDialogFragment().apply {
            isCancelable = true//设置点击外部是否可以取消
        }
    }
    val updateTodoBottomSheet by lazy {
        UpdateDialogFragment().apply {
            isCancelable = true//设置点击外部是否可以取消
        }
    }
    private val userViewModel by lazy { ViewModelProvider(this)[UserViewModel::class.java] }
    private val addTodoViewModel by lazy { ViewModelProvider(this)[AddTodoFragmentViewModel::class.java] }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dateViewModel = ViewModelProvider(this)[DateViewModel::class.java]
        binding = DataBindingUtil.setContentView(this, R.layout.activity_date)
        binding.apply {
            activityDateViewPagerMonth.adapter =
                MonthAdapter(this@DateActivity, TOTAL_MONTH)
            //旋转180度，让其向左排列月份；同时在MonthFragment中让里面的内容翻转180度,以正常显示内容
            activityDateViewPagerMonth.rotationY = 180f
            activityDateViewPagerMonth.currentItem = TOTAL_MONTH / 2
            activityDateViewPagerDay.adapter =
                DayInfoAdapter(this@DateActivity, DateCalculator.getDays(0))
            //控制另外addButton和listChangeButton的出现和消失
            activityDateMenuButton.rotation = 90f
            activityDateMenuButton.setOnLongClickListener {
                isMenuExpand = if (isMenuExpand) {
                    activityDateListChangeButton.animate().xBy(200f).withEndAction {
                        activityDateListChangeButton.visibility = View.INVISIBLE
                    }
                    activityDateAddButton.animate().xBy(400f).withEndAction {
                        activityDateAddButton.visibility = View.INVISIBLE
                    }
                    activityDateMenuButton.animate().rotation(90f)
                    false
                } else {
                    activityDateAddButton.visibility = View.VISIBLE
                    activityDateListChangeButton.visibility = View.VISIBLE
                    activityDateListChangeButton.animate().xBy(-200f)
                    activityDateAddButton.animate().xBy(-400f)
                    activityDateMenuButton.animate().rotation(0f)
                    true
                }
                true
            }
            //新增TodoButton
            activityDateAddButton.setOnClickListener {
                addTodoBottomSheet.show(supportFragmentManager, FRAGMENT_ADD_TODO)
                addTodoViewModel.emitShowingState(true)
            }
            //完成/未完成Todo切换Button
            activityDateListChangeButton.setOnClickListener {
                dateViewModel.changeQueryTodoState()
                activityDateListChangeButton.animate().rotationBy(90f)
            }
            //跳转今日Button
            activityDateTodayButton.setOnClickListener {
                DateCalculator.currentDate.value = todayDate
                if (lastMonthVpPosition != TOTAL_MONTH / 2) {
                    activityDateViewPagerMonth.currentItem = TOTAL_MONTH / 2
                    lastMonthVpPosition = TOTAL_MONTH / 2
                    activityDateNumberMonth.resetToCurrentMonth()
                    activityDateViewPagerMonth.adapter?.notifyDataSetChanged()
                }
            }
            activityDateCollapseLayout.collapseListener = {
                dateViewModel.emitCollapsedState(DateViewModel.CollapsedState.COLLAPSED)
            }
            activityDateCollapseLayout.verticalScrollingListener = {
                dateViewModel.emitCollapsedState(DateViewModel.CollapsedState.SCROLLING)
            }
            activityDateCollapseLayout.expandListener = {
                dateViewModel.emitCollapsedState(DateViewModel.CollapsedState.EXPAND)
            }
            activityDateImgUser.setOnClickListener {
                if (UserRepository.user.value != null) {
                    UserFragment().show(supportFragmentManager, FRAGMENT_USER)
                } else {
                    LoginFragment().show(supportFragmentManager, FRAGMENT_LOGIN)
                }
                userViewModel.emitShowState(true)
            }
            //监听上方月份VP的滑动，根据滑动切换activityDateViewPagerMonth的值
            activityDateViewPagerMonth.registerOnPageChangeCallback(object :
                ViewPager2.OnPageChangeCallback() {
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int,
                ) {
                    //1为RightToLeft,0为LeftToRight
                    val direction = if (lastMonthVpPosition == position) 0 else 1
                    activityDateNumberMonth.translate(
                        direction,
                        positionOffset
                    )
                }

                override fun onPageSelected(position: Int) {
                    //改变年份
                    val calendar = Calendar.getInstance()
                    calendar.add(Calendar.MONTH, TOTAL_MONTH / 2 - position)
                    yearTextWidth = binding.activityDateNumberYear.width
                    if (calendar[Calendar.YEAR] < dateViewModel.currentYear) {
                        binding.activityDateNumberYear.animateToLastYear(calendar)
                    } else if (calendar[Calendar.YEAR] > dateViewModel.currentYear) {
                        binding.activityDateNumberYear.animateToNextYear(calendar)
                    }
                    dateViewModel.currentYear = calendar[Calendar.YEAR]
                    dateViewModel.currentMonth = calendar[Calendar.MONTH]
                    //改变月份
                    if (position > lastMonthVpPosition) {
                        activityDateNumberMonth.changePosition(1)
                    } else if (position < lastMonthVpPosition) {
                        activityDateNumberMonth.changePosition(0)
                    }
                    lastMonthVpPosition = position
                }
            })
            bindTodoManagerService()
            //初始化下方vp的数据
            dateViewModel.emitDays("${dateViewModel.currentYear}-${dateViewModel.currentMonth}")
            //对当前vp应该显示日期的监听，当接收到数据时，切换到对应item
            safeLaunch {
                DateCalculator.currentDate.collectLatest {
                    val currentYear = it.split("-")[0].toInt()
                    val currentMonth = it.split("-")[1].toInt()
                    val currentDay = it.split("-")[2].toInt()
                    if (currentYear != lastYear || currentMonth != lastMonth) {
                        //当切换了月份后点击日期,需要改变下方vp中的值
                        dateViewModel.emitDays("${currentYear}-${currentMonth}")
                    }
                    if (it != todayDate) {
                        activityDateTodayButton.animate().alpha(1f)
                    }
                    if (it == todayDate) {
                        activityDateTodayButton.animate().alpha(0f)
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
                dateViewModel.dayFragmentDays.collectLatest {
                    if (it.size > 1) {//在初始化flow的时候会发送一个listOf("")，需要排除这种情况
                        DiffUtil.calculateDiff(
                            DayInfoAdapter.DayDiffUtil(
                                (activityDateViewPagerDay.adapter as DayInfoAdapter).days,
                                it
                            )
                        ).dispatchUpdatesTo(activityDateViewPagerDay.adapter as DayInfoAdapter)
                        (activityDateViewPagerDay.adapter as DayInfoAdapter).days = it
                    }
                }
            }
            //对折叠状态进行监听，改变中间Button的状态
            safeLaunch {
                dateViewModel.collapsedState.collectLatest {
                    binding.activityDateMenuButton.collapsedStateAnimate(it)
                }
            }
            //对用户界面是否展示进行监听，改变右上角用户图标的可见度
            safeLaunch {
                userViewModel.isShowing.collectLatest {
                    if (it) binding.activityDateImgUser.animate()
                        .alpha(0f) else binding.activityDateImgUser.animate().alpha(1f)
                }
            }
            //对AddTodo界面进行监听，改变中间fab的状态
            safeLaunch {
                addTodoViewModel.isShowing.collectLatest {
                    if (it) {//正在展示AddTodo界面
                        binding.activityDateMenuButton.addTodoAnimate(this@DateActivity)
                        binding.activityDateAddButton.animate().alpha(0f)
                    }
                    else {
                        binding.activityDateMenuButton.cancelAddTodoAnimate()
                        binding.activityDateAddButton.animate().alpha(1f)
                    }
                }
            }
        }
    }

    //绑定启动TodoManagerService
    private fun bindTodoManagerService() {
        val todoManagerServiceConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName, service: IBinder) {
                BaseApp.todoManagerBinder = TodoManager.Stub.asInterface(service)
            }

            override fun onServiceDisconnected(name: ComponentName?) {}
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            bindService(Intent(this@DateActivity, TodoManagerService::class.java),
                todoManagerServiceConnection,
                Context.BIND_AUTO_CREATE)
        }

    }

    /**通过动画让年份切换到上一年*/
    private fun TextView.animateToLastYear(calendar: Calendar) =
        animate().x(yearTextWidth / 2f).alpha(0f)
            .withEndAction {
                binding.activityDateNumberYear.text =
                    calendar[Calendar.YEAR].toString()
                x = -yearTextWidth / 2f
                animate().x(0f).alpha(1f)
            }

    /**通过动画让年份切换到下一年*/
    private fun TextView.animateToNextYear(calendar: Calendar) =
        animate().x(-yearTextWidth / 2f).alpha(0f)
            .withEndAction {
                binding.activityDateNumberYear.text =
                    calendar[Calendar.YEAR].toString()
                x = yearTextWidth / 2f
                animate().x(0f).alpha(1f)
            }
}