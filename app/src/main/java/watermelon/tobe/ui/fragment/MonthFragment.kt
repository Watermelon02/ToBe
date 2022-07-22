package watermelon.tobe.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.flow.collectLatest
import watermelon.tobe.databinding.FragmentMonthBinding
import watermelon.tobe.ui.adapter.DaysAdapter
import watermelon.tobe.util.extension.safeLaunch
import watermelon.tobe.view.MonthlyViewLayoutManager
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
    private val dateViewModel: DateViewModel by lazy { ViewModelProvider(requireActivity())[DateViewModel::class.java] }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            FragmentMonthBinding.inflate(LayoutInflater.from(context), null, false)
        binding.apply {
            fragmentMonthRecyclerviewDay.recycledViewPool.setMaxRecycledViews(0, 50)
            fragmentMonthRecyclerviewDay.adapter =
                DaysAdapter(dateViewModel.dayFragmentDays.value)
            binding.fragmentMonthRecyclerviewDay.setMonthLayoutManager = {
                fragmentMonthRecyclerviewDay.layoutManager =
                    MonthlyViewLayoutManager(context, 7)
                fragmentMonthRecyclerviewDay.adapter?.notifyDataSetChanged()
            }
            binding.fragmentMonthRecyclerviewDay.setWeekLayoutManager = {
                fragmentMonthRecyclerviewDay.layoutManager =
                    WeeklyViewLayoutManager(
                        requireContext(),
                        LinearLayoutManager.HORIZONTAL,
                        false
                    )
                fragmentMonthRecyclerviewDay.adapter?.notifyDataSetChanged()
            }
            viewLifecycleOwner.safeLaunch {
                dateViewModel.collapsedState.collect {
                    fragmentMonthRecyclerviewDay.collapsedState = it
                    fragmentMonthInnerScrollLayout.collapsedState = it
                    if (it == DateViewModel.CollapsedState.COLLAPSED && fragmentMonthRecyclerviewDay.layoutManager !is WeeklyViewLayoutManager) {
                        fragmentMonthRecyclerviewDay.layoutManager =
                            WeeklyViewLayoutManager(
                                requireContext(),
                                LinearLayoutManager.HORIZONTAL,
                                false
                            )
                        fragmentMonthRecyclerviewDay.adapter?.notifyDataSetChanged()
                    } else if (it == DateViewModel.CollapsedState.EXPAND && fragmentMonthRecyclerviewDay.layoutManager !is MonthlyViewLayoutManager) {
                        fragmentMonthRecyclerviewDay.layoutManager =
                            MonthlyViewLayoutManager(context, 7)
                        fragmentMonthRecyclerviewDay.adapter?.notifyDataSetChanged()
                        if (fragmentMonthRecyclerviewDay.height != fragmentMonthInnerScrollLayout.expandedHeight) {
                            fragmentMonthRecyclerviewDay.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                                height = fragmentMonthInnerScrollLayout.expandedHeight
                            }
                        }
                    }
                    monthFragmentViewModel.emitDays(month)
                }
            }
            //让里面的内容翻转180度,以避免在DateActivity中的翻转让内部内容变成镜像
            root.rotationY = 180f
        }
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