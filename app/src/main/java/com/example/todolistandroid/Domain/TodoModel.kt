package com.example.todolistandroid.Domain

import java.io.Serializable

data class TodoModel(
    val id: Int,
    var text: String,
    var isDone: Boolean = false
) : Serializable
