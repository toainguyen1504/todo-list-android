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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todolistandroid.Adapter.TodoAdapter
import com.example.todolistandroid.Domain.TodoModel
import com.example.todolistandroid.R
import com.example.todolistandroid.databinding.ActivityMainBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.util.ArrayList

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: TodoAdapter
    private val mockTodos = mutableListOf<TodoModel>()

    // Khai báo launcher để nhận kết quả trả về
    private val deleteLauncher = registerForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val updatedTodos =
                result.data?.getSerializableExtra("updatedTodos") as? ArrayList<TodoModel>
            if (updatedTodos != null) {
                mockTodos.clear()
                mockTodos.addAll(updatedTodos)
                adapter.notifyDataSetChanged()
                updateSubtitle()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val window: Window = this@MainActivity.window
        window.statusBarColor = ContextCompat.getColor(this@MainActivity, R.color.primary)


        // handle show list todo (mock data) to view
        val recyclerView = binding.taskView
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Mock data
        repeat(10) { i ->
            mockTodos.add(TodoModel(id = i + 1, text = "Task số ${i + 1}"))
        }
        updateSubtitle()

        adapter = TodoAdapter(mockTodos) { todo ->
            // when click 1 todo
            showEditTodoDialog(todo.text) { newText ->
                todo.text = newText
                recyclerView.adapter?.notifyItemChanged(mockTodos.indexOf(todo))
            }
        }
        recyclerView.adapter = adapter

        // add todo: handle click add btn event
        binding.addBtn.setOnClickListener {
            showEditTodoDialog { newTask ->
                val newTodo = TodoModel(
                    id = mockTodos.size + 1, // or Random ID
                    text = newTask,
                    isDone = false
                )

                mockTodos.add(0, newTodo)

                // alert adapter that it has new item
                adapter.notifyItemInserted(0)
                binding.taskView.scrollToPosition(0)
                updateSubtitle()

                Toast.makeText(this, "Added: $newTask", Toast.LENGTH_SHORT).show()
            }
        }


        // handle click setting button
        val settingBtn = findViewById<ImageButton>(R.id.setting)

        settingBtn.setOnClickListener { view ->
            val popup =  PopupMenu(
                ContextThemeWrapper(this, R.style.CustomPopupMenu),
                view
            )
            popup.menuInflater.inflate(R.menu.menu_setting, popup.menu)

            // divider line
            popup.menu.setGroupDividerEnabled(true)

            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_delete -> {
                        val intent = Intent(this, DeleteTodoActivity::class.java)
                        intent.putExtra("todos", ArrayList(mockTodos) as java.io.Serializable)

                        // use launcher , not startActivity
                        deleteLauncher.launch(intent)
                        true
                    }

                    R.id.action_hide_completed -> {
                        Toast.makeText(this, "Hide clicked", Toast.LENGTH_SHORT).show()
                        true
                    }

                    else -> false
                }
            }
            popup.show()
        }
    }

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
        }  else {
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

        // Focus + open keyboard
//        editText.requestFocus()
//        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
//        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)

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

        // focus và show keyboard
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
    private fun updateSubtitle() {
        binding.subtitle.text = "${mockTodos.size} tasks"
    }
}
