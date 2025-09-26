package com.example.todolistandroid.Domain

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "todos_table")
data class TodoModel(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var text: String,
    var isDone: Boolean = false,
    var isSelected: Boolean = false
) : Serializable
