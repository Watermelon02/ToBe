package watermelon.tobe.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.coroutines.flow.collectLatest
import watermelon.tobe.util.extension.safeLaunch
import watermelon.tobe.databinding.FragmentDayBinding
import watermelon.tobe.viewmodel.DateViewModel

/**
 * description ： TODO:类的作用
 * author : Watermelon02
 * email : 1446157077@qq.com
 * date : 2022/7/14 13:33
 */
class DayFragment(val time: String, val viewModel: DateViewModel) : Fragment() {
    @SuppressLint("SetTextI18n")
    private lateinit var binding:FragmentDayBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            FragmentDayBinding.inflate(LayoutInflater.from(context), null, false)
        val timeList = time.split("-")
        val year = timeList[0].toInt()
        val month = timeList[1].toInt()
        val day = timeList[2].toInt()
        //去除yyyy,取MM-dd
        binding.fragmentDayTitle.text = "${month}-${day}"
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        viewLifecycleOwner.safeLaunch {
            //获取日期对应的数据
            viewModel.queryHoliday(time).collectLatest {
                binding.fragmentDayLunarCalendar.text = it?.lunarCalendar
                binding.fragmentDaySuit.text = it?.suit
            }
        }
    }
}