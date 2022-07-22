package watermelon.tobe.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import watermelon.tobe.databinding.ItemTodoBinding
import watermelon.tobe.repo.bean.Todo
import watermelon.tobe.viewmodel.DayFragmentViewModel

/**
 * author : Watermelon02
 * email : 1446157077@qq.com
 * date : 2022/7/19 13:29
 */
class TodoAdapter(var todoList: List<Todo>, val viewModel: DayFragmentViewModel,val updateTodoListener:(todo:Todo)->Unit) :
    RecyclerView.Adapter<TodoAdapter.ViewHolder>() {
    class ViewHolder(val binding: ItemTodoBinding) : RecyclerView.ViewHolder(binding.root){
        var isRemoved = false
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(ItemTodoBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {
            itemTodoTitle.text = todoList[position].title
            itemTodoContent.text = todoList[position].content
            itemTodoBlueButton.setColor(Color.BLUE)
            itemTodoRedButton.setColor(Color.RED)
            itemTodoRedButton.setOnClickListener {
                viewModel.deleteTodo(todoList[position])
                holder.isRemoved = true
            }
            itemTodoBlueButton.setOnClickListener{
                updateTodoListener(todoList[position])
            }
        }
    }

    override fun onViewDetachedFromWindow(holder: ViewHolder) {
        if (holder.isRemoved){
            holder.binding.root.animate().alpha(0f).withEndAction {
                holder.binding.root.alpha = 1f
                holder.isRemoved = false
            }
        }
        super.onViewDetachedFromWindow(holder)
    }

    override fun getItemCount(): Int = todoList.size
}