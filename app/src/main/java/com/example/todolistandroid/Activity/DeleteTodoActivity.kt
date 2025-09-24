package com.example.todolistandroid.Activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todolistandroid.Adapter.DeleteAdapter
import com.example.todolistandroid.Domain.TodoModel
import com.example.todolistandroid.databinding.ActivityTodoDeleteBinding

class DeleteTodoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTodoDeleteBinding
    private val todos = mutableListOf<TodoModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTodoDeleteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // get todos from MainActivity
        val data = intent.getSerializableExtra("todos") as? ArrayList<TodoModel>
        if (data != null) todos.addAll(data)

        // Setup RecyclerView
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = DeleteAdapter(todos) { selectedTodo ->
            Toast.makeText(this, "Deleted: ${selectedTodo.text}", Toast.LENGTH_SHORT).show()
        }

    }

}