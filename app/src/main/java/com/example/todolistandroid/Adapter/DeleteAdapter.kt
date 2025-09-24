package com.example.todolistandroid.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
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
        val checkBox: ImageView = itemView.findViewById(R.id.todoCheckBox)

        val todoRow: LinearLayout = itemView.findViewById(R.id.todoRow)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DeleteAdapter.DeleteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.viewholder_delete_todo, parent, false)
        return DeleteViewHolder(view)
    }

    override fun onBindViewHolder(holder: DeleteAdapter.DeleteViewHolder, position: Int) {
        val todo = todos[position]
        holder.text.text = todo.text

        val context = holder.itemView.context

        // set color and background
        if (todo.isDone) {
            holder.text.setTextColor(context.getColor(R.color.semi_grey))
            holder.todoRow.setBackgroundResource(R.drawable.grey_background)
        } else {
            holder.checkBox.setColorFilter(context.getColor(R.color.semi_grey))
            holder.todoRow.setBackgroundResource(R.drawable.white_background)
        }

        // show icon checkbox
        if (todo.isSelected) {
            holder.checkBox.setImageResource(R.drawable.check_box)
            holder.checkBox.setColorFilter(context.getColor(R.color.primary))
        } else {
            holder.checkBox.setImageResource(R.drawable.check_box_blank)
            holder.checkBox.setColorFilter(context.getColor(R.color.semi_grey))
        }

        // click checkbox -> toggle
        holder.checkBox.setOnClickListener {
            val pos = holder.bindingAdapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                todos[pos].isSelected = !todos[pos].isSelected
                notifyItemChanged(pos)
            }
        }

    }

    override fun getItemCount(): Int = todos.size


    fun selectAll(select: Boolean) {
        todos.forEach { it.isSelected = select }
        notifyDataSetChanged()
    }
}