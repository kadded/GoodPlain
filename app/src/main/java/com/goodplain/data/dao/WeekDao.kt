package com.goodplan.app.data.dao

import androidx.room.*
import com.goodplan.app.data.entity.Task
import com.goodplan.app.data.entity.WeekGoal
import com.goodplan.app.data.entity.WeekNote
import kotlinx.coroutines.flow.Flow

@Dao
interface WeekDao {
    @Query("SELECT * FROM week_goals WHERE weekStart = :weekStart LIMIT 1")
    fun getWeekGoal(weekStart: String): Flow<WeekGoal?>

    @Upsert
    suspend fun upsertWeekGoal(goal: WeekGoal)

    @Query("SELECT * FROM tasks WHERE date = :date ORDER BY position ASC")
    fun getTasksForDate(date: String): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE date BETWEEN :from AND :to ORDER BY date ASC, position ASC")
    fun getTasksForWeek(from: String, to: String): Flow<List<Task>>

    @Insert
    suspend fun insertTask(task: Task): Long

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    @Query("DELETE FROM tasks WHERE date = :date")
    suspend fun deleteTasksForDate(date: String)

    @Query("SELECT * FROM week_notes WHERE date = :date LIMIT 1")
    fun getNote(date: String): Flow<WeekNote?>

    @Upsert
    suspend fun upsertNote(note: WeekNote)
}