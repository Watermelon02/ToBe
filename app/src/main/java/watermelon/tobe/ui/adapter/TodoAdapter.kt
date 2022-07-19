package watermelon.tobe.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import watermelon.tobe.databinding.ItemTodoBinding
import watermelon.tobe.repo.bean.Todo

/**
 * author : Watermelon02
 * email : 1446157077@qq.com
 * date : 2022/7/19 13:29
 */
class TodoAdapter(var todoList: List<Todo>) : RecyclerView.Adapter<TodoAdapter.ViewHolder>() {
    class ViewHolder(val binding: ItemTodoBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(ItemTodoBinding.inflate(LayoutInflater.from(parent.context)))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {
            itemTodoTitle.text = todoList[position].title
            itemTodoContent.text = todoList[position].content
        }
    }

    override fun getItemCount(): Int = todoList.size
}