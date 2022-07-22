package watermelon.tobe.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import watermelon.tobe.ui.fragment.MonthFragment
import java.util.*

/**
 * description ： DateActivity上方的vp2对应的adapter
 * author : Watermelon02
 * email : 1446157077@qq.com
 * date : 2022/7/14 18:56
 * @param diffMonth 从该用户使用软件的月份到当前月份的差
 */
class MonthAdapter(fragmentActivity: FragmentActivity, private val diffMonth: Int) :
    FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = diffMonth

    override fun createFragment(position: Int): Fragment {
        val calendar = Calendar.getInstance().apply {
            add(Calendar.MONTH,-position+diffMonth/2)
        }
        return MonthFragment("${calendar[Calendar.YEAR]}-"+"${calendar[Calendar.MONTH]+1}")
    }
}