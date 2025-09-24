package com.example.todolistandroid.Activity

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todolistandroid.Adapter.DeleteAdapter
import com.example.todolistandroid.Domain.TodoModel
import com.example.todolistandroid.R
import com.example.todolistandroid.databinding.ActivityTodoDeleteBinding

class DeleteTodoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTodoDeleteBinding
    private val todos = mutableListOf<TodoModel>()
    private lateinit var adapter: DeleteAdapter
    private var isAllSelected = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTodoDeleteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // get todos from MainActivity
        val data = intent.getSerializableExtra("todos") as? ArrayList<TodoModel>
        if (data != null) todos.addAll(data)

        adapter = DeleteAdapter(todos) { selectedTodo ->
            Toast.makeText(this, "Deleted: ${selectedTodo.text}", Toast.LENGTH_SHORT).show()
        }

        binding.taskView.layoutManager = LinearLayoutManager(this)
        binding.taskView.adapter = adapter

        // btnSelect: select/deselect all
        binding.btnSelect.setOnClickListener {
            isAllSelected = !isAllSelected
            adapter.selectAll(isAllSelected)

            // change text btnSelect
            binding.btnSelect.text = if (isAllSelected) "Deselect all" else "Select all"

            // change title
            binding.title.text = if (isAllSelected) "All selected" else getString(R.string.delete_title)
        }

        val btnCancel = findViewById<TextView>(R.id.btnCancel)
        btnCancel.setOnClickListener {
            finish()
        }

        // todo handle logic:  delete
        binding.btnDeleteIcon.setOnClickListener {
            // get selected todos
            val selectedTodos = todos.filter { it.isSelected }

            if (selectedTodos.isEmpty()) {
                Toast.makeText(this, "No tasks selected to delete", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // delete all selected todos
            todos.removeAll(selectedTodos)

            // back result
            val resultIntent = intent
            resultIntent.putExtra("updatedTodos", ArrayList(todos))
            setResult(RESULT_OK, resultIntent)

            // reset state selectAll
            isAllSelected = false
            binding.btnSelect.text = "Select all"
            binding.title.text = getString(R.string.delete_title)

            Toast.makeText(this, "Deleted ${selectedTodos.size} task(s)", Toast.LENGTH_SHORT).show()

            finish()
        }

    }
}

