package com.goodplan.app.data.dao

import androidx.room.*
import com.goodplan.app.data.entity.Habit
import com.goodplan.app.data.entity.HabitEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {
    @Query("SELECT * FROM habits ORDER BY position ASC")
    fun getAllHabits(): Flow<List<Habit>>

    @Insert
    suspend fun insertHabit(habit: Habit): Long

    @Update
    suspend fun updateHabit(habit: Habit)

    @Delete
    suspend fun deleteHabit(habit: Habit)

    @Query("SELECT * FROM habit_entries WHERE habitId = :habitId AND date BETWEEN :from AND :to")
    fun getEntries(habitId: Long, from: String, to: String): Flow<List<HabitEntry>>

    @Query("SELECT * FROM habit_entries WHERE date BETWEEN :from AND :to")
    fun getAllEntriesInRange(from: String, to: String): Flow<List<HabitEntry>>

    @Upsert
    suspend fun upsertEntry(entry: HabitEntry)

    @Query("DELETE FROM habit_entries WHERE habitId = :habitId AND date = :date")
    suspend fun deleteEntry(habitId: Long, date: String)
}