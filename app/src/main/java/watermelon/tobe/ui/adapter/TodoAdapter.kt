package watermelon.tobe.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import watermelon.lightmusic.util.extension.toast
import watermelon.tobe.databinding.ItemTodoBinding
import watermelon.tobe.repo.bean.Todo
import watermelon.tobe.viewmodel.DayFragmentViewModel

/**
 * author : Watermelon02
 * email : 1446157077@qq.com
 * date : 2022/7/19 13:29
 */
class TodoAdapter(var todoList: List<Todo>, val viewModel: DayFragmentViewModel) :
    RecyclerView.Adapter<TodoAdapter.ViewHolder>() {
    class ViewHolder(val binding: ItemTodoBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(ItemTodoBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {
            itemTodoTitle.text = todoList[position].title
            itemTodoContent.text = todoList[position].content
            itemTodoBlueButton.setColor(Color.BLUE)
            itemTodoRedButton.setColor(Color.RED)
            itemTodoRedButton.setOnClickListener {
                toast("delete")
//                viewModel.deleteTodo(todoList[position])
            }
            itemTodoBlueButton.setOnClickListener {

            }
        }
    }


    override fun getItemCount(): Int = todoList.size
}