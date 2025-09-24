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
    private val todos: List<TodoModel>,
    private val onClick: (TodoModel) -> Unit
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
        holder.checkIcon.setImageResource(
            if (todo.isDone) R.drawable.check_circle
            else R.drawable.check_circle_blank
        )

        // handle click
        holder.todoText.setOnClickListener { onClick(todo) }
    }

    override fun getItemCount(): Int = todos.size
}