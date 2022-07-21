package watermelon.tobe.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import watermelon.tobe.databinding.ItemAddTodoBinding

/**
 * description ： AddTodoFragment中下方的时间选择vp
 * author : Watermelon02
 * email : 1446157077@qq.com
 * date : 2022/7/21 10:27
 */
class AddTodoTimeAdapter(private val timeList: List<Int>) :
    RecyclerView.Adapter<AddTodoTimeAdapter.ViewHolder>() {
    class ViewHolder(val binding: ItemAddTodoBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAddTodoBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        binding.root.rotation = 90f
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.itemAddTodoTime.text = timeList[position].toString()
    }

    override fun getItemCount() = timeList.size
}