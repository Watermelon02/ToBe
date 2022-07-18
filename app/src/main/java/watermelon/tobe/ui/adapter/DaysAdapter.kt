package watermelon.tobe.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import watermelon.tobe.databinding.ItemDayBinding
import watermelon.tobe.repo.bean.Day
import watermelon.tobe.util.local.DateCalculator

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(ItemDayBinding.inflate(LayoutInflater.from(parent.context)))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (days[position].date != "") {
            //判断当前day是否属于本月，不属于则将日期颜色变灰
            val isCurrentMonthDay =
                days[position].date.split("-")[1] == days.last().date.split("-")[1]
            //设置Item的Text为日期
            holder.dateBinding.activityDateItemDayDate.text = days[position].date.split("-")[2]
            if (days[position].type == 2) {//为节假日
                holder.dateBinding.activityDateItemDayFestival.text =
                    days[position].typeDes.substring(0,2)
            }else if (days[position].solarTerms.length == 2){//为节气（==2是因为存在 "小雪后" 这种返回值）
                holder.dateBinding.activityDateItemDayFestival.text =
                    days[position].solarTerms
            }else{
                if (days[position].lunarCalendar.isNotEmpty()){//防止本地生成数据时为空的情况
                    holder.dateBinding.activityDateItemDayFestival.text =
                        days[position].lunarCalendar.substring(days[position].lunarCalendar.length-2,days[position].lunarCalendar.length)
                }
            }
            if (isCurrentMonthDay) {
                holder.dateBinding.root.setOnClickListener {
                    Log.d("testTag", "(DaysAdapter.kt:32) -> ${days[holder.adapterPosition].date}")
                    //更改flow的值,使DateActivity下方的日期详情vp跳转,本来应该在onCreateViewHolder里面绑定，防止重复设置点击监听.但是会出现adapterPosition为
                    DateCalculator.viewPagerDayCurrentItem.value = days[holder.adapterPosition].date
                }
            } else {
                holder.dateBinding.activityDateItemDayDate.alpha = 0.2f
                holder.dateBinding.activityDateItemDayFestival.alpha = 0.2f
            }
        }
    }

    override fun getItemCount(): Int = days.size
}