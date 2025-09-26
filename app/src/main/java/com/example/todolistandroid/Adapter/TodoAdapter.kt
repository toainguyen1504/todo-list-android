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
    private val onToggle: (TodoModel) -> Unit,
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

        // show checkbox status
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

        // click checkbox -> call callback to activity update DB
        holder.todoText.setOnClickListener { onClick(todo) }

        holder.checkIcon.setOnClickListener {
            onToggle(todo)
            onTodoUpdated()
        }

    }

    override fun getItemCount(): Int = todos.size

    fun setTodos(newTodos: List<TodoModel>) {
        todos.clear()
        todos.addAll(newTodos)
        notifyDataSetChanged()
    }

    fun getCurrentTodos(): List<TodoModel> = todos
}