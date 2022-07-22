package watermelon.tobe.ui.adapter

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.viewpager2.adapter.FragmentStateAdapter
import watermelon.tobe.ui.fragment.DayFragment
import watermelon.tobe.repo.bean.Day
import watermelon.tobe.ui.activity.DateActivity

/**
 * description ： DateActivity下方的vp2对应的adapter
 * author : Watermelon02
 * email : 1446157077@qq.com
 * date : 2022/7/14 16:01
 */
class DayInfoAdapter(dateActivity: DateActivity, var days:List<Day>) : FragmentStateAdapter(dateActivity) {
    override fun getItemCount(): Int = days.size

    override fun createFragment(position: Int): Fragment {
        return DayFragment(days[position].date)
    }

    /**
     * description ： 用于DateActivity下方vp2的差分刷新
     * author : Watermelon02
     * email : 1446157077@qq.com
     * date : 2022/7/14 19:30
     */
    class DayDiffUtil(private val oldDay: List<Day>, private val newDay: List<Day>) :
        DiffUtil.Callback() {
        override fun getOldListSize(): Int {
            return oldDay.size
        }

        override fun getNewListSize(): Int {
            return newDay.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldDay[oldItemPosition] == newDay[newItemPosition]
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return (oldDay[oldItemPosition] == newDay[newItemPosition])
        }

    }
}