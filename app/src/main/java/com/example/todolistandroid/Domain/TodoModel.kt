package com.example.todolistandroid.Domain

data class TodoModel(
    val id: Int,
    var text: String,
    var isDone: Boolean = false
)
