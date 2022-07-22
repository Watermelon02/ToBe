package watermelon.tobe.ui.fragment

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import watermelon.tobe.databinding.FragmentAddTodoBinding
import watermelon.tobe.ui.adapter.AddTodoTimeAdapter
import watermelon.tobe.viewmodel.DateViewModel
import watermelon.tobe.viewmodel.UpdateTodoFragmentViewModel

/**
 * author : Watermelon02
 * email : 1446157077@qq.com
 * date : 2022/7/21 17:13
 */
class UpdateDialogFragment: BottomSheetDialogFragment() {
    private lateinit var binding: FragmentAddTodoBinding
    private val updateTodoFragmentViewModel: UpdateTodoFragmentViewModel by lazy { ViewModelProvider(requireActivity())[UpdateTodoFragmentViewModel::class.java] }
    private val dateActivityViewModel: DateViewModel by lazy { ViewModelProvider(requireActivity())[DateViewModel::class.java] }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddTodoBinding.inflate(inflater, container, false)
        binding.fragmentAddTodoCreateTodo.text = "Update Todo"
        initTimeViewPagers()
        binding.fragmentAddTodoCancel.setOnClickListener {
            dismiss()
        }
        binding.fragmentAddTodoSubmit.setOnClickListener {
            updateTodoFragmentViewModel.todo.title = binding.fragmentAddTodoTitleEdit.text.toString()
            updateTodoFragmentViewModel.todo.content = binding.fragmentAddTodoContentEdit.text.toString()
            updateTodoFragmentViewModel.updateTodo()
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
                updateTodoFragmentViewModel.year = YEAR_LIST[position]
            }
        })
        binding.fragmentAddTodoMonth.adapter = AddTodoTimeAdapter(MONTH_LIST)
        binding.fragmentAddTodoMonth.rotation = -90f
        binding.fragmentAddTodoMonth.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateTodoFragmentViewModel.month = MONTH_LIST[position]
            }
        })
        binding.fragmentAddTodoDay.adapter = AddTodoTimeAdapter(DAY_LIST)
        binding.fragmentAddTodoDay.rotation = -90f
        binding.fragmentAddTodoDay.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateTodoFragmentViewModel.day = DAY_LIST[position]
            }
        })
    }

    override fun onResume() {
        super.onResume()
        binding.fragmentAddTodoTitleEdit.text = Editable.Factory.getInstance().newEditable(updateTodoFragmentViewModel.todo.title)
        binding.fragmentAddTodoContentEdit.text = Editable.Factory.getInstance().newEditable(updateTodoFragmentViewModel.todo.content)
        binding.fragmentAddTodoYear.currentItem = updateTodoFragmentViewModel.year - YEAR_LIST[0]
        binding.fragmentAddTodoMonth.currentItem = updateTodoFragmentViewModel.month - MONTH_LIST[0]
        binding.fragmentAddTodoDay.currentItem = updateTodoFragmentViewModel.day - DAY_LIST[0]
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
            2,
            26,
            27,
            28,
            29,
            30,
            31
        )
    }
}