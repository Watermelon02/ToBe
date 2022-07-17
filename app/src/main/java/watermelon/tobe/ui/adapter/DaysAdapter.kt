package watermelon.tobe.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import watermelon.tobe.databinding.ItemDayBinding
import watermelon.tobe.repo.bean.Day
import watermelon.tobe.util.local.DateCalculator
import watermelon.tobe.view.CollapseDayItem
import watermelon.tobe.viewmodel.DateViewModel

/**
 * description ： DateActivity中的日历
 * author : Watermelon02
 * email : 1446157077@qq.com
 * date : 2022/7/14 17:00
 */
class DaysAdapter(var days: List<Day>) :
    RecyclerView.Adapter<DaysAdapter.ViewHolder>() {
    open class ViewHolder(val dateBinding: ItemDayBinding) :
        RecyclerView.ViewHolder(dateBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewHolder = ViewHolder(ItemDayBinding.inflate(LayoutInflater.from(parent.context)))
        viewHolder.dateBinding.root.setOnClickListener {
            //更改flow的值,使DateActivity下方的日期详情vp跳转
            DateCalculator.viewPagerDayCurrentItem.value = days[viewHolder.adapterPosition].date
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //设置Item的Text为日期
        holder.dateBinding.moduleDateActivityDateItemDay.text = days[position].date.split("-")[2]
    }

    override fun getItemCount(): Int = days.size
}