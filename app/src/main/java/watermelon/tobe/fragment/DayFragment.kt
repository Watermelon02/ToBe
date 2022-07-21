package watermelon.tobe.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.flow.collectLatest
import watermelon.lightmusic.util.extension.toast
import watermelon.tobe.databinding.FragmentDayBinding
import watermelon.tobe.ui.activity.DateActivity
import watermelon.tobe.ui.adapter.TodoAdapter
import watermelon.tobe.util.extension.safeLaunch
import watermelon.tobe.viewmodel.DateViewModel
import watermelon.tobe.viewmodel.DayFragmentViewModel
import watermelon.tobe.viewmodel.UpdateTodoFragmentViewModel

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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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
            }else if (it.title == "Empty"){
                toast("没有Todo,无法修改TvT")
            }else if (it.title == "Loading"){
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
        viewLifecycleOwner.safeLaunch {
            dayFragmentViewModel.todoList.collectLatest {
                (binding.fragmentDayTodoList.adapter as TodoAdapter).todoList = it
                (binding.fragmentDayTodoList.adapter as TodoAdapter).notifyDataSetChanged()
            }
        }
        viewLifecycleOwner.safeLaunch { //监听是否有新增Todo
            dateActivityViewModel.isTodoListChange.collectLatest {
                dayFragmentViewModel.queryTodoList(time)
            }
        }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        dayFragmentViewModel.queryTodoList(time)
    }
}