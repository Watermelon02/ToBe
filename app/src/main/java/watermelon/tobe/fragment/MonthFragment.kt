package watermelon.tobe.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import watermelon.tobe.view.StickGridLayoutManager
import watermelon.tobe.databinding.FragmentMonthBinding
import watermelon.tobe.ui.adapter.DaysAdapter
import watermelon.tobe.util.local.DateCalculator
import watermelon.tobe.viewmodel.DateViewModel
import java.util.*

/**
 * description ： TODO:类的作用
 * author : Watermelon02
 * email : 1446157077@qq.com
 * date : 2022/7/14 13:33
 */
class MonthFragment(val time: String) : Fragment() {
    private val year = time.split("-")[0].toInt()
    private val month = time.split("-")[1].toInt()
    private lateinit var monthBinding:FragmentMonthBinding
    private lateinit var viewModel : DateViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        monthBinding =
            FragmentMonthBinding.inflate(LayoutInflater.from(context), null, false)
        viewModel = DateViewModel()
        monthBinding.apply {
            //因为month为序数，所以这里+1
//            moduleDateFragmentMonthMonth.text = "$year-${month+1}"
            moduleDateFragmentMonthRecyclerviewDay.layoutManager =
                StickGridLayoutManager(context, 7)
            moduleDateFragmentMonthRecyclerviewDay.adapter =
                DaysAdapter(DateCalculator.getDays(calculateDiffMonth()))
            //让里面的内容翻转180度,以避免在DateActivity中的翻转让内部内容变成镜像
            root.rotationY = 180f
        }
        return monthBinding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    @SuppressLint("SimpleDateFormat")
    private fun calculateDiffMonth(): Int {
        val currentYear = DateCalculator.calendar2[Calendar.YEAR]
        //因为calendar[Calendar.Month]获取到的月份为当前月份-1,所以这里也-1
        val currentMonth = DateCalculator.calendar2[Calendar.MONTH]
        return if (currentMonth > month) {
            -((currentMonth - month) + 12 * (currentYear - year))
        } else {
            -((currentMonth + 12 - month) + 12 * (currentYear - year - 1))
        }
    }
}