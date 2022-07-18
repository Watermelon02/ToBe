package watermelon.tobe.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import kotlinx.coroutines.flow.collectLatest
import watermelon.tobe.databinding.FragmentMonthBinding
import watermelon.tobe.ui.adapter.DaysAdapter
import watermelon.tobe.util.extension.safeLaunch
import watermelon.tobe.view.StickGridLayoutManager
import watermelon.tobe.view.WeeklyViewLayoutManager
import watermelon.tobe.viewmodel.DateViewModel
import watermelon.tobe.viewmodel.MonthFragmentViewModel

/**
 * description ： TODO:类的作用
 * author : Watermelon02
 * email : 1446157077@qq.com
 * date : 2022/7/14 13:33
 */
class MonthFragment(
    private val month: String,
) : Fragment() {
    private lateinit var binding: FragmentMonthBinding
    private val monthFragmentViewModel by lazy { ViewModelProvider(this)[MonthFragmentViewModel::class.java] }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dateViewModel = ViewModelProvider(requireActivity())[DateViewModel::class.java]
        binding =
            FragmentMonthBinding.inflate(LayoutInflater.from(context), null, false)
        binding.apply {
            fragmentMonthRecyclerviewDay.recycledViewPool.setMaxRecycledViews(0,50)
            fragmentMonthRecyclerviewDay.layoutManager =
                WeeklyViewLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            fragmentMonthRecyclerviewDay.adapter =
                DaysAdapter(listOf())
            viewLifecycleOwner.safeLaunch {
                dateViewModel.collapsedState.collectLatest {
                    fragmentMonthRecyclerviewDay.collapsedState = it
                    if (it == DateViewModel.CollapsedState.COLLAPSED) {
                        fragmentMonthRecyclerviewDay.layoutManager =
                            WeeklyViewLayoutManager(
                                requireContext(),
                                LinearLayoutManager.HORIZONTAL,
                                false
                            )
                    } else if (it == DateViewModel.CollapsedState.EXPAND) {
                        fragmentMonthRecyclerviewDay.layoutManager =
                            StickGridLayoutManager(context, 7)
                    }
                    fragmentMonthRecyclerviewDay.adapter =
                        DaysAdapter(listOf())
                    monthFragmentViewModel.emitDays(month)
                }
            }
            //让里面的内容翻转180度,以避免在DateActivity中的翻转让内部内容变成镜像
            root.rotationY = 180f
        }
        monthFragmentViewModel.emitDays(month)
        viewLifecycleOwner.safeLaunch {
            monthFragmentViewModel.days.collectLatest {
                it.let {
                    (binding.fragmentMonthRecyclerviewDay.adapter as DaysAdapter).days =
                        it
                    binding.fragmentMonthRecyclerviewDay.adapter?.notifyDataSetChanged()
                }
            }
        }
        return binding.root
    }
}