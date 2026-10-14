package com.goodplan.app.repository

import com.goodplan.app.data.AppDatabase
import com.goodplan.app.data.entity.*
import kotlinx.coroutines.flow.Flow

class AppRepository(private val db: AppDatabase) {
    val weekDao = db.weekDao()
    val habitDao = db.habitDao()

    fun weekGoal(weekStart: String): Flow<WeekGoal?> = weekDao.getWeekGoal(weekStart)
    suspend fun saveWeekGoal(g: WeekGoal) = weekDao.upsertWeekGoal(g)

    fun tasksForDate(date: String): Flow<List<Task>> = weekDao.getTasksForDate(date)
    fun tasksForWeek(from: String, to: String): Flow<List<Task>> = weekDao.getTasksForWeek(from, to)
    suspend fun addTask(t: Task): Long = weekDao.insertTask(t)
    suspend fun updateTask(t: Task) = weekDao.updateTask(t)
    suspend fun deleteTask(t: Task) = weekDao.deleteTask(t)
    suspend fun clearTasksForDate(date: String) = weekDao.deleteTasksForDate(date)

    fun note(date: String): Flow<WeekNote?> = weekDao.getNote(date)
    suspend fun saveNote(n: WeekNote) = weekDao.upsertNote(n)

    fun habits(): Flow<List<Habit>> = habitDao.getAllHabits()
    suspend fun addHabit(h: Habit): Long = habitDao.insertHabit(h)
    suspend fun updateHabit(h: Habit) = habitDao.updateHabit(h)
    suspend fun deleteHabit(h: Habit) = habitDao.deleteHabit(h)

    fun entries(habitId: Long, from: String, to: String): Flow<List<HabitEntry>> =
        habitDao.getEntries(habitId, from, to)

    fun allEntriesInRange(from: String, to: String): Flow<List<HabitEntry>> =
        habitDao.getAllEntriesInRange(from, to)

    suspend fun toggleEntry(habitId: Long, date: String, completed: Boolean) {
        if (completed) {
            habitDao.upsertEntry(HabitEntry(habitId = habitId, date = date, completed = true))
        } else {
            habitDao.deleteEntry(habitId, date)
        }
    }
}