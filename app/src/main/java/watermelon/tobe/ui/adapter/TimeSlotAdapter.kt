package watermelon.tobe.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import watermelon.tobe.databinding.ItemAddTodoBinding
import watermelon.tobe.databinding.ItemTimeSlotBinding

/**
 * description ： AddTodo界面下发的时间段选择Vo的adapter,用于配合ClockView设置小时
 * author : Watermelon02
 * email : 1446157077@qq.com
 * date : 2022/7/23 13:58
 */
class TimeSlotAdapter(private val timeList: List<Int>) :
    RecyclerView.Adapter<TimeSlotAdapter.ViewHolder>() {
    class ViewHolder(val binding: ItemTimeSlotBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemTimeSlotBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //显示上午，下午时间段
        holder.binding.itemTimeSlot.text = if (timeList[position] == 0) "AM"
        else "PM"
    }

    override fun getItemCount() = timeList.size
}