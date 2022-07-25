package watermelon.tobe.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import watermelon.tobe.databinding.FragmentAddTodoBinding
import watermelon.tobe.service.aidl.Todo
import watermelon.tobe.ui.adapter.AddTodoTimeAdapter
import watermelon.tobe.ui.adapter.TimeSlotAdapter
import watermelon.tobe.util.local.DateCalculator
import watermelon.tobe.viewmodel.AddTodoFragmentViewModel
import watermelon.tobe.viewmodel.DateViewModel
import java.util.*
import kotlin.math.absoluteValue

/**
 * description ： TODO:类的作用
 * author : Watermelon02
 * email : 1446157077@qq.com
 * date : 2022/7/20 21:34
 */
class AddTodoDialogFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentAddTodoBinding
    private val addTodoFragmentViewModel: AddTodoFragmentViewModel by lazy {
        ViewModelProvider(
            requireActivity()
        )[AddTodoFragmentViewModel::class.java]
    }
    private val dateActivityViewModel: DateViewModel by lazy { ViewModelProvider(requireActivity())[DateViewModel::class.java] }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddTodoBinding.inflate(inflater, container, false)
        initTimeViewPagers()
        binding.fragmentAddTodoCancel.setOnClickListener {
            dismiss()
        }
        binding.fragmentAddTodoSubmit.setOnClickListener {
            addTodoFragmentViewModel.title = binding.fragmentAddTodoTitleEdit.text.toString()
            addTodoFragmentViewModel.content = binding.fragmentAddTodoContentEdit.text.toString()
            addTodoFragmentViewModel.addTodo()
            dateActivityViewModel.emitTodoListChange()
            dismiss()
        }
        return binding.root
    }

    //初始化时间选择vp们，为他们注册监听和跳转到当日
    private fun initTimeViewPagers() {
        binding.fragmentAddTodoYear.adapter = AddTodoTimeAdapter(YEAR_LIST)
        binding.fragmentAddTodoYear.rotation = -90f
        binding.fragmentAddTodoYear.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                addTodoFragmentViewModel.year = YEAR_LIST[position]
            }
        })
        binding.fragmentAddTodoMonth.adapter = AddTodoTimeAdapter(MONTH_LIST)
        binding.fragmentAddTodoMonth.rotation = -90f
        binding.fragmentAddTodoMonth.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                addTodoFragmentViewModel.month = MONTH_LIST[position]
            }
        })
        binding.fragmentAddTodoDay.adapter = AddTodoTimeAdapter(DAY_LIST)
        binding.fragmentAddTodoDay.rotation = -90f
        binding.fragmentAddTodoDay.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                addTodoFragmentViewModel.day = DAY_LIST[position]
            }
        })
        val calendar = DateCalculator.todayCalendar
        if (addTodoFragmentViewModel.year == 0) {//对时间初始化到当天
            binding.fragmentAddTodoYear.currentItem = calendar[Calendar.YEAR] - YEAR_LIST[0]
            binding.fragmentAddTodoMonth.currentItem = calendar[Calendar.MONTH] + 1 - MONTH_LIST[0]
            binding.fragmentAddTodoDay.currentItem = calendar[Calendar.DATE] - DAY_LIST[0]
        }
        binding.fragmentAddTodoHour.releaseListener = { degree ->
            val hour = if (degree>0){
                (degree/30).toInt()
            }else{
                12-(degree/30).toInt().absoluteValue
            }
            addTodoFragmentViewModel.hour = (TIME_SLOT_LIST[binding.fragmentAddTodoTime.currentItem]+hour)
        }
        binding.fragmentAddTodoTime.adapter = TimeSlotAdapter(TIME_SLOT_LIST)
    }

    override fun onDestroy() {
        addTodoFragmentViewModel.title = binding.fragmentAddTodoTitleEdit.text.toString()
        addTodoFragmentViewModel.content = binding.fragmentAddTodoContentEdit.text.toString()
        super.onDestroy()
        addTodoFragmentViewModel.emitShowingState(false)
    }

    companion object {
        private val YEAR_LIST = listOf(2022, 2023, 2024, 2025, 2026)
        private val MONTH_LIST = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12)
        private val DAY_LIST = listOf(
            1,
            2,
            3,
            4,
            5,
            6,
            7,
            8,
            9,
            10,
            11,
            12,
            13,
            14,
            15,
            16,
            17,
            18,
            19,
            20,
            21,
            22,
            23,
            24,
            25,
            26,
            27,
            28,
            29,
            30,
            31
        )
        private val TIME_SLOT_LIST = listOf(0, 12)
    }
}