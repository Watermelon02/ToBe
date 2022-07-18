package watermelon.tobe.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.coroutines.flow.collectLatest
import watermelon.tobe.databinding.FragmentMonthBinding
import watermelon.tobe.ui.adapter.DaysAdapter
import watermelon.tobe.util.extension.safeLaunch
import watermelon.tobe.view.StickGridLayoutManager
import watermelon.tobe.viewmodel.DateViewModel

/**
 * description ： TODO:类的作用
 * author : Watermelon02
 * email : 1446157077@qq.com
 * date : 2022/7/14 13:33
 */
class MonthFragment(private val time: String, private val viewModel: DateViewModel) : Fragment() {
    private lateinit var monthBinding: FragmentMonthBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        monthBinding =
            FragmentMonthBinding.inflate(LayoutInflater.from(context), null, false)
        monthBinding.apply {
            moduleDateFragmentMonthRecyclerviewDay.layoutManager =
                StickGridLayoutManager(context, 7)

            moduleDateFragmentMonthRecyclerviewDay.adapter =
                DaysAdapter(listOf())
            //让里面的内容翻转180度,以避免在DateActivity中的翻转让内部内容变成镜像
            root.rotationY = 180f
        }
        viewLifecycleOwner.safeLaunch {
            viewModel.queryMonth(time).collectLatest {
                it?.let {
                    (monthBinding.moduleDateFragmentMonthRecyclerviewDay.adapter as DaysAdapter).days =
                        it
                    monthBinding.moduleDateFragmentMonthRecyclerviewDay.adapter?.notifyDataSetChanged()
                }
            }
        }
        viewLifecycleOwner.safeLaunch {
            viewModel.collapsedState.collectLatest {
                monthBinding.moduleDateFragmentMonthRecyclerviewDay.collapsedState = it
            }
        }
        return monthBinding.root
    }
}