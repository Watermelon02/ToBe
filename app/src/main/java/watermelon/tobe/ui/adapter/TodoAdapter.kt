package watermelon.tobe.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import watermelon.tobe.databinding.ItemTodoBinding
import watermelon.tobe.service.aidl.Todo
import watermelon.tobe.util.extension.toast
import watermelon.tobe.viewmodel.DateViewModel
import watermelon.tobe.viewmodel.DayFragmentViewModel

/**
 * author : Watermelon02
 * email : 1446157077@qq.com
 * date : 2022/7/19 13:29
 */
class TodoAdapter(
    var queryTodoState: DateViewModel.QueryTodoState,
    var todoList: List<Todo>,
    val viewModel: DayFragmentViewModel,
    val updateTodoListener: (todo: Todo) -> Unit,
) :
    RecyclerView.Adapter<TodoAdapter.ViewHolder>() {
    class ViewHolder(val binding: ItemTodoBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(ItemTodoBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {
            itemTodoTitle.text = todoList[position].title
            if (todoList[position].priority != -1) {
                itemTodoHour.text = "(${todoList[position].priority}:00)"
            }
            itemTodoContent.text = todoList[position].content
            itemTodoLeftButton.setColor(Color.RED)
            itemTodoLeftButton.setOnClickListener {
                viewModel.deleteTodo(todoList[position])
            }
            if (queryTodoState == DateViewModel.QueryTodoState.NOT_FINISHED) {
                itemTodoRightButton.setColor(Color.BLUE)
                itemTodoRightButton.setOnClickListener {
                    itemTodoRightButton.animate().alpha(0f).setDuration(400).withEndAction {
                        itemTodoRightButton.setColor(Color.GREEN)
                        itemTodoRightButton.animate().alpha(1f)
                    }
                    viewModel.finishTodo(todoList[position])
                }
                itemTodoDragView.doubleClickListener = {
                    updateTodoListener(todoList[position])
                }
            } else {
                itemTodoRightButton.setColor(Color.GREEN)
                itemTodoRightButton.setOnClickListener {
                    itemTodoRightButton.animate().alpha(0f).setDuration(400).withEndAction {
                        itemTodoRightButton.setColor(Color.GREEN)
                        itemTodoRightButton.animate().alpha(1f)
                    }
                    toast("Todo已经被完成啦！")
                }
            }
        }
    }

    override fun getItemCount(): Int = todoList.size

    /**
     * description ： 用于TodoList的差分刷新
     * author : Watermelon02
     * email : 1446157077@qq.com
     * date : 2022/7/14 19:30
     */
    class TodoDiffUtil(private val oldTodo: List<Todo>, private val newTodo: List<Todo>) :
        DiffUtil.Callback() {
        override fun getOldListSize(): Int {
            return oldTodo.size
        }

        override fun getNewListSize(): Int {
            return newTodo.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldTodo[oldItemPosition].id == newTodo[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return ((oldTodo[oldItemPosition].title == newTodo[newItemPosition].title) &&
                    (oldTodo[oldItemPosition].content == newTodo[newItemPosition].content) &&
                    (oldTodo[oldItemPosition].dateStr == newTodo[newItemPosition].dateStr))
        }

    }
}