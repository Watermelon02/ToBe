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
import watermelon.tobe.util.extension.safeLaunch
import watermelon.tobe.databinding.FragmentDayBinding
import watermelon.tobe.ui.adapter.TodoAdapter
import watermelon.tobe.viewmodel.DateViewModel
import watermelon.tobe.viewmodel.DayFragmentViewModel

/**
 * description ： TODO:类的作用
 * author : Watermelon02
 * email : 1446157077@qq.com
 * date : 2022/7/14 13:33
 */
class DayFragment(private val time: String) : Fragment() {
    @SuppressLint("SetTextI18n")
    private lateinit var binding: FragmentDayBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            FragmentDayBinding.inflate(LayoutInflater.from(context), null, false)
        val dateActivityViewModel: DateViewModel =
            ViewModelProvider(requireActivity())[DateViewModel::class.java]
        val dayFragmentViewModel: DayFragmentViewModel =
            ViewModelProvider(this)[DayFragmentViewModel::class.java]
        val timeList = time.split("-")
        val year = timeList[0].toInt()
        val month = timeList[1].toInt()
        val day = timeList[2].toInt()
        //去除yyyy,取MM-dd
        binding.fragmentDayTitle.text = "${month}-${day}"
        binding.fragmentDayTodoList.adapter = TodoAdapter(listOf(),dayFragmentViewModel)
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
            dayFragmentViewModel.queryTodoList(time)
            dayFragmentViewModel.todoList.collectLatest {
                (binding.fragmentDayTodoList.adapter as TodoAdapter).todoList = it
                (binding.fragmentDayTodoList.adapter as TodoAdapter).notifyDataSetChanged()
            }
        }
        return binding.root
    }

}