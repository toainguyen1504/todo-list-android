package com.example.todolistandroid.Activity

import android.content.Intent
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todolistandroid.Adapter.TodoAdapter
import com.example.todolistandroid.Data.TodoDatabase
import com.example.todolistandroid.Data.TodoDao
import com.example.todolistandroid.Domain.TodoModel
import com.example.todolistandroid.R
import com.example.todolistandroid.databinding.ActivityMainBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: TodoAdapter
    private lateinit var todoDao: TodoDao
    private lateinit var db: TodoDatabase

    // Save lastest snapshot  from DB
    private var latestTodos: List<TodoModel> = emptyList()
    private var isHideCompleted = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val window: Window = this@MainActivity.window
        window.statusBarColor = ContextCompat.getColor(this@MainActivity, R.color.primary)

        // init db + dao
        db = TodoDatabase.getDatabase(this)
        todoDao = db.todoDao()

        // create adapter and pass onToggle to update DB when checkbox is clicked
        adapter = TodoAdapter(
            todos = mutableListOf(),
            onClick = { todo ->
                showEditTodoDialog(todo.text) { newText ->
                    lifecycleScope.launch { todoDao.update(todo.copy(text = newText)) }
                }
            },
            onToggle = { todo ->
                // toggle isDone status and update into DB -> Flow will emit the new list
                lifecycleScope.launch {
                    todoDao.update(todo.copy(isDone = !todo.isDone))
                }
            },
            onTodoUpdated = { /* if adapter has a callback when state changes, update DB here */ }
        )

        binding.taskView.layoutManager = LinearLayoutManager(this)
        binding.taskView.adapter = adapter

        // collect data from Flow (collectLatest is better for UI)
        lifecycleScope.launch {
            todoDao.getAllTodos().collect { todos ->
                latestTodos = todos

                val finalDisplay = getDisplayTodos(todos)

                adapter.setTodos(finalDisplay)
                updateSubtitle(finalDisplay)
            }
        }

        // add todo
        binding.addBtn.setOnClickListener {
            showEditTodoDialog { newTask ->
                val newTodo = TodoModel(text = newTask)
                lifecycleScope.launch { todoDao.insert(newTodo) }

                binding.taskView.scrollToPosition(0)
                Toast.makeText(this, "Added: $newTask", Toast.LENGTH_SHORT).show()
            }
        }


        // handle click setting button
        val settingBtn = findViewById<ImageButton>(R.id.setting)

        settingBtn.setOnClickListener { view ->
            val popup = PopupMenu(
                ContextThemeWrapper(this, R.style.CustomPopupMenu),
                view
            )
            popup.menuInflater.inflate(R.menu.menu_setting, popup.menu)

            // divider line
            popup.menu.setGroupDividerEnabled(true)

            // set initial title
            val hideCompletedItem = popup.menu.findItem(R.id.action_hide_completed)
            hideCompletedItem.title = if (isHideCompleted) "Show completed" else "Hide completed"

            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_delete -> {
                        // open delete view
                        val currentDisplay = adapter.getCurrentTodos()  // viết hàm này trong adapter
                        val intent = Intent(this, DeleteTodoActivity::class.java)
                        intent.putExtra("todos", ArrayList(currentDisplay))
                        startActivityForResult(intent, 1001)
                        true
                    }
                    R.id.action_hide_completed -> {
                        isHideCompleted = !isHideCompleted // toggle
                        hideCompletedItem.title =
                            if (isHideCompleted) "Show completed" else "Hide completed"

                        val finalDisplay = getDisplayTodos(latestTodos)
                        adapter.setTodos(finalDisplay)
                        updateSubtitle(finalDisplay)
                        true
                    }

                    else -> false
                }
            }
                popup.show()
        }
    }   // end onCreate


        // function show add and edit popup
        private fun showEditTodoDialog(oldText: String? = null, onSave: (String) -> Unit) {
            val bottomSheet = BottomSheetDialog(this)
            val view = layoutInflater.inflate(R.layout.dialog_edit_todo, null)
            bottomSheet.setContentView(view)
            bottomSheet.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)


            val editText = view.findViewById<EditText>(R.id.inputTodoText)
            val btnCancel = view.findViewById<TextView>(R.id.btnCancel)
            val btnSave = view.findViewById<TextView>(R.id.btnSave)
            val dialogTitle = view.findViewById<TextView>(R.id.dialogTitle)

            // Initial state : disable Save
            btnSave.isEnabled = false
            btnSave.alpha = 0.5f

            // Case edit
            if (!oldText.isNullOrEmpty()) {
                dialogTitle.text = getString(R.string.edit_to_do_title)
                editText.setText(oldText)
                editText.setSelection(editText.text.length)

                // check state
                editText.addTextChangedListener() {
                    val currentText = it.toString().trim()
                    if (currentText.isNotEmpty() && currentText != oldText.trim()) {
                        btnSave.isEnabled = true
                        btnSave.alpha = 1f
                    } else {
                        btnSave.isEnabled = false
                        btnSave.alpha = 0.5f
                    }
                }
            } else {
                // Case Add
                dialogTitle.text = getString(R.string.create_to_do_title)

                // check state
                editText.addTextChangedListener {
                    val currentText = it.toString().trim()
                    if (currentText.isNotEmpty()) {
                        btnSave.isEnabled = true
                        btnSave.alpha = 1f
                    } else {
                        btnSave.isEnabled = false
                        btnSave.alpha = 0.5f
                    }
                }
            }

            btnCancel.setOnClickListener { bottomSheet.dismiss() }

            btnSave.setOnClickListener {
                val newTask = editText.text.toString().trim()
                if (newTask.isNotEmpty()) {
                    onSave(newTask)
                }
                bottomSheet.dismiss()
            }

            bottomSheet.setOnShowListener { dialog ->
                try {
                    val d = dialog as BottomSheetDialog
                    val bottomSheetView =
                        d.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
                    bottomSheetView?.setBackgroundResource(R.drawable.dialog_background)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            // focus and show keyboard
            editText?.let {
                it.requestFocus()
                it.post {
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.showSoftInput(it, InputMethodManager.SHOW_IMPLICIT)
                }
            }

            bottomSheet.show()
        }

        // function update total of todos
        private fun updateSubtitle(todos: List<TodoModel>) {
            val undoneCount = todos.count { !it.isDone }
            val doneCount = todos.count { it.isDone }

            binding.subtitle.text = when {
                undoneCount == 0 && doneCount > 0 -> "All tasks completed!"
                doneCount > 0 -> "$undoneCount tasks (doing), $doneCount done"
                else -> "$undoneCount tasks"
            }
        }

        //    override onActivityResult to update DB after delete
        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)

            if (requestCode == 1001 && resultCode == RESULT_OK) {
                val updatedTodos = data?.getSerializableExtra("updatedTodos") as? ArrayList<TodoModel>

                if (updatedTodos != null) {
                    lifecycleScope.launch {
                        val updatedIds = updatedTodos.map { it.id }
                        val deleted = latestTodos.filter { it.id !in updatedIds }
                        todoDao.deleteByIds(deleted.map { it.id })
                    }
                }
            }
        }

        // get todos list done or !done
        private fun getDisplayTodos(source: List<TodoModel>): List<TodoModel> {
            val undone = source.filter { !it.isDone }
            val done = source.filter { it.isDone }

            // undone todos stay position (id DESC), done todos goes to the bottom
            val display = undone + done

            return if (isHideCompleted) undone else display
        }

}

