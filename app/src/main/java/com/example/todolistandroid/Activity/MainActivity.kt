package com.example.todolistandroid.Activity

import android.os.Bundle
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.todolistandroid.R
import com.example.todolistandroid.databinding.ActivityMainBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val window: Window = this@MainActivity.window
        window.statusBarColor = ContextCompat.getColor(this@MainActivity, R.color.primary)

        binding.addBtn.setOnClickListener {
            showEditTodoDialog { newTask ->
                // TODO: logic thêm task vào danh sách
                // Logic thêm To-do mới
                Toast.makeText(this, "Added: $newTask", Toast.LENGTH_SHORT).show()
            }
        }
    }

    //  function show add and edit popop
    private fun showEditTodoDialog(oldText: String? = null, onSave: (String) -> Unit) {
        val bottomSheet = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.dialog_edit_todo, null)
        bottomSheet.setContentView(view)

        val editText = view.findViewById<EditText>(R.id.editTextTodo)
        val btnCancel = view.findViewById<TextView>(R.id.btnCancel)
        val btnSave = view.findViewById<TextView>(R.id.btnSave)

        // it is edit -> fill old text
        if (!oldText.isNullOrEmpty()) {
            editText.setText(oldText)
            editText.setSelection(editText.text.length)
        }

        // Focus + open keyboard
        editText.requestFocus()
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)

        btnCancel.setOnClickListener { bottomSheet.dismiss() }
        btnSave.setOnClickListener {
            val newTask = editText.text.toString().trim()
            if (newTask.isNotEmpty()) {
                onSave(newTask)
            }
            bottomSheet.dismiss()
        }

        bottomSheet.show()
    }

}