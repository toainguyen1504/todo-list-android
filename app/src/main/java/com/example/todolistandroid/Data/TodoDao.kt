package com.example.todolistandroid.Data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.todolistandroid.Domain.TodoModel
import kotlinx.coroutines.flow.Flow
import androidx.room.*

@Dao
interface TodoDao {
    @Query("SELECT * FROM todos_table ORDER BY id DESC")
    fun getAllTodos(): Flow<List<TodoModel>>

    @Query("SELECT * FROM todos_table WHERE isDone = 0")
    suspend fun getActiveTodos(): List<TodoModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(todo: TodoModel)

    @Update
    suspend fun update(todo: TodoModel)

    @Delete
    suspend fun delete(todo: TodoModel)

    @Query("DELETE FROM todos_table")
    suspend fun deleteAll()

    @Query("DELETE FROM todos_table WHERE id IN (:ids)")
    suspend fun deleteByIds(ids: List<Int>)

}