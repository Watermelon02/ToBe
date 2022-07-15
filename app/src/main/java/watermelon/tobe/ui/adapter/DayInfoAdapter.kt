package watermelon.tobe.ui.adapter

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.viewpager2.adapter.FragmentStateAdapter
import watermelon.tobe.fragment.DayFragment
import watermelon.tobe.ui.activity.DateActivity

/**
 * description ： DateActivity下方的vp2对应的adapter
 * author : Watermelon02
 * email : 1446157077@qq.com
 * date : 2022/7/14 16:01
 */
class DayInfoAdapter(val dateActivity: DateActivity, var days:List<String>) : FragmentStateAdapter(dateActivity) {
    override fun getItemCount(): Int = days.size

    override fun createFragment(position: Int): Fragment {
        return DayFragment(days[position],dateActivity.viewModel)
    }

    /**
     * description ： 用于DateActivity下方vp2的差分刷新，
     * 直接adapter.notifyDataSetChanged()好像会因为FragmentStateAdapter缓存的原因而保留三个之前月份的Note
     * author : Watermelon02
     * email : 1446157077@qq.com
     * date : 2022/7/14 19:30
     */
    class NoteDiffUtil(private val oldPets: List<String>, private val newPets: List<String>) :
        DiffUtil.Callback() {
        override fun getOldListSize(): Int {
            return oldPets.size
        }

        override fun getNewListSize(): Int {
            return newPets.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldPets[oldItemPosition] == newPets[newItemPosition]
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return (oldPets[oldItemPosition] == newPets[newItemPosition])
        }

    }
}