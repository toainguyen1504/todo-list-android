package com.example.todolistandroid.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.todolistandroid.Domain.TodoModel
import com.example.todolistandroid.R

class DeleteAdapter (
    private val todos: MutableList<TodoModel>,
    private val onDeleteClick: (TodoModel) -> Unit
    ) : RecyclerView.Adapter<DeleteAdapter.DeleteViewHolder>() {
    inner class DeleteViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        val text: TextView = itemView.findViewById(R.id.todoText)
        val checkBox: CheckBox = itemView.findViewById(R.id.todoCheckBox)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DeleteAdapter.DeleteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_delete_todo, parent, false)
        return DeleteViewHolder(view)
    }

    override fun onBindViewHolder(holder: DeleteAdapter.DeleteViewHolder, position: Int) {
        val todo = todos[position]
        holder.text.text = todo.text
        holder.checkBox.isChecked = false

        // when click checkbox -> call delete callback
        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                onDeleteClick(todo)
                // remove
                val removedIndex = holder.adapterPosition
                todos.removeAt(removedIndex)
                notifyItemRemoved(removedIndex)
            }
        }
    }

    override fun getItemCount(): Int = todos.size
}