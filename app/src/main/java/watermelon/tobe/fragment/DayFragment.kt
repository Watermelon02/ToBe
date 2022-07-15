package watermelon.tobe.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import watermelon.tobe.util.extension.safeLaunch
import watermelon.tobe.databinding.FragmentDayBinding
import watermelon.tobe.viewmodel.DateViewModel

/**
 * description ： TODO:类的作用
 * author : Watermelon02
 * email : 1446157077@qq.com
 * date : 2022/7/14 13:33
 */
class DayFragment(val time: String,val viewModel: DateViewModel) : Fragment() {
    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val monthBinding =
            FragmentDayBinding.inflate(LayoutInflater.from(context), null, false)
        //去除yyyy,取MM-dd
        monthBinding.moduleDateFragmentDayTitle.text = time.substring(5)
        safeLaunch {
            //获取日期对应的数据
            viewModel.queryDay(time)
                .collect {
                    monthBinding.moduleDateFragmentDayTitle.text = "${time.split("-")[1].toInt()+1}-"+time.split("-")[2]
                    monthBinding.moduleDateFragmentDayText.text = it?.content
                }
        }
        return monthBinding.root
    }
}