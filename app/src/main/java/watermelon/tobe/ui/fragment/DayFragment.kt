package watermelon.tobe.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.flow.collectLatest
import watermelon.tobe.util.extension.toast
import watermelon.tobe.databinding.FragmentDayBinding
import watermelon.tobe.ui.activity.DateActivity
import watermelon.tobe.ui.adapter.TodoAdapter
import watermelon.tobe.util.extension.safeLaunch
import watermelon.tobe.util.local.DateCalculator
import watermelon.tobe.viewmodel.DateViewModel
import watermelon.tobe.viewmodel.DayFragmentViewModel
import watermelon.tobe.viewmodel.UpdateTodoFragmentViewModel
import watermelon.tobe.viewmodel.UserViewModel

/**
 * description ： TODO:类的作用
 * author : Watermelon02
 * email : 1446157077@qq.com
 * date : 2022/7/14 13:33
 */
class DayFragment(private val time: String) : Fragment() {
    @SuppressLint("SetTextI18n")
    private lateinit var binding: FragmentDayBinding
    private val dayFragmentViewModel: DayFragmentViewModel by lazy { ViewModelProvider(this)[DayFragmentViewModel::class.java] }

    //当网络连接失败导致登录失败时，设置为true
    private var connectFailure = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dayFragmentViewModel.queryTodoList(time)
        binding =
            FragmentDayBinding.inflate(LayoutInflater.from(context), null, false)
        val dateActivityViewModel: DateViewModel =
            ViewModelProvider(requireActivity())[DateViewModel::class.java]
        val timeList = time.split("-")
        val year = timeList[0].toInt()
        val month = timeList[1].toInt()
        val day = timeList[2].toInt()
        //去除yyyy,取MM-dd
        binding.fragmentDayTitle.text = "${month}-${day}"
        binding.fragmentDayTodoList.adapter = TodoAdapter(listOf(), dayFragmentViewModel) {
            if (it.dateStr != "") {
                //设置点击Todo的蓝色按钮的监听，进入update T odo 界面，并将该todo的数据传给界面
                val updateTodoFragmentViewModel =
                    ViewModelProvider(requireActivity())[UpdateTodoFragmentViewModel::class.java]
                updateTodoFragmentViewModel.year = it.dateStr.split("-")[0].toInt()
                updateTodoFragmentViewModel.month = it.dateStr.split("-")[1].toInt()
                updateTodoFragmentViewModel.day = it.dateStr.split("-")[2].toInt()
                updateTodoFragmentViewModel.todo = it
                (activity as DateActivity).updateTodoBottomSheet.show(
                    parentFragmentManager,
                    "up_date"
                )
            } else if (it.title == "Empty") {
                toast("没有Todo,无法修改TvT")
            } else if (it.title == "Loading") {
                toast("加载中，请稍后~~")
            }
        }
        binding.fragmentDayTodoList.layoutManager = LinearLayoutManager(requireContext())
        viewLifecycleOwner.safeLaunch {
            //监听日期数据
            dateActivityViewModel.dayFragmentDays.collectLatest {
                if (it.isNotEmpty()) {
                    var monthBeginning = 0
                    for (i in it.indices) {
                        if (it[i].date.split("-")[2] == "1") {
                            monthBeginning = i
                        }
                    }
                    binding.fragmentDayLunarCalendar.text =
                        it[monthBeginning - 1 + day].lunarCalendar
                    binding.fragmentDaySuit.text = it[monthBeginning - 1 + day].suit
                }
            }
        }
        //当todoList刷新时，执行刷新动画
        viewLifecycleOwner.safeLaunch {
            dayFragmentViewModel.todoList.collectLatest {
                binding.fragmentDayTodoList.animate().alpha(0f).withEndAction {
                    (binding.fragmentDayTodoList.adapter as TodoAdapter).todoList = it
                    (binding.fragmentDayTodoList.adapter as TodoAdapter).notifyDataSetChanged()
                    binding.fragmentDayTodoList.animate().alpha(1f)
                }
            }
        }
        viewLifecycleOwner.safeLaunch { //监听是否有新增Todo
            dateActivityViewModel.isTodoListChange.collectLatest {
                dayFragmentViewModel.queryTodoList(time)
            }
        }
        viewLifecycleOwner.safeLaunch {
            ViewModelProvider(requireActivity())[UserViewModel::class.java].user.collectLatest {
                if (it?.errorCode != 0) {
                    connectFailure = true
                } else if (connectFailure && it.errorCode == 0) {
                    //如果连接失败后，又再次登录成功，则发起网络请求
                    dayFragmentViewModel.queryTodoList(time)
                }

            }
        }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        DateCalculator.currentDate.value = time
    }
}