package com.example.todolistandroid.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.todolistandroid.Domain.TodoModel
import com.example.todolistandroid.R

class TodoAdapter (
    private val todos: MutableList<TodoModel>,
    private val onClick: (TodoModel) -> Unit,
    private val onTodoUpdated: () -> Unit

) : RecyclerView.Adapter<TodoAdapter.TodoViewHolder>(){
    inner class TodoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val checkIcon: ImageView = itemView.findViewById(R.id.radio_check)
        val todoText: TextView = itemView.findViewById(R.id.editTodoText)
        val todoRow: LinearLayout = itemView.findViewById(R.id.editTodoRow)

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TodoAdapter.TodoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.viewholder_todos, parent, false)
        return TodoViewHolder(view)
    }

    override fun onBindViewHolder(holder: TodoAdapter.TodoViewHolder, position: Int) {
        val todo = todos[position]
        holder.todoText.text = todo.text

        val context = holder.itemView.context

        // show icon checkbox
        if (todo.isDone) {
            holder.checkIcon.setImageResource(R.drawable.check_circle)
            holder.checkIcon.setColorFilter(context.getColor(R.color.primary))
            holder.todoRow.setBackgroundResource(R.drawable.grey_background)
            holder.todoText.setTextColor(context.getColor(R.color.semi_grey))
        } else {
            holder.checkIcon.setImageResource(R.drawable.check_circle_blank)
            holder.checkIcon.setColorFilter(context.getColor(R.color.semi_grey))
            holder.todoRow.setBackgroundResource(R.drawable.white_background)
            holder.todoText.setTextColor(context.getColor(R.color.black))
        }

        // handle click
        holder.todoText.setOnClickListener { onClick(todo) }

        holder.checkIcon.setOnClickListener {
            val pos = holder.bindingAdapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                // Toggle trạng thái done / doing
                todo.isDone = !todo.isDone

                // Cập nhật icon và background ngay lập tức
                if (todo.isDone) {
                    holder.checkIcon.setImageResource(R.drawable.check_circle)
                    holder.checkIcon.setColorFilter(context.getColor(R.color.primary))
                    holder.todoRow.setBackgroundResource(R.drawable.grey_background)
                    holder.todoText.setTextColor(context.getColor(R.color.semi_grey))

                    // Remove khỏi vị trí cũ và thêm vào cuối list
                    todos.removeAt(pos)
                    notifyItemRemoved(pos)
                    todos.add(todo)
                    notifyItemInserted(todos.size - 1)
                } else {
                    holder.checkIcon.setImageResource(R.drawable.check_circle_blank)
                    holder.checkIcon.setColorFilter(context.getColor(R.color.semi_grey))
                    holder.todoRow.setBackgroundResource(R.drawable.white_background)
                    holder.todoText.setTextColor(context.getColor(R.color.black))

                    // Remove khỏi vị trí cũ và thêm lên đầu list
                    todos.removeAt(pos)
                    notifyItemRemoved(pos)
                    todos.add(0, todo)
                    notifyItemInserted(0)
                }

                // Cập nhật subtitle
                onTodoUpdated()
            }
        }

    }

    override fun getItemCount(): Int = todos.size
}