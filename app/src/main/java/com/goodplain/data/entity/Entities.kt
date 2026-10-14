package com.goodplan.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "week_goals")
data class WeekGoal(
    @PrimaryKey val weekStart: String, // yyyy-MM-dd (понедельник)
    val goal1: String = "",
    val goal2: String = "",
    val goal3: String = "",
    val mainResult: String = ""
)

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,          // yyyy-MM-dd
    val text: String,
    val isCompleted: Boolean = false,
    val position: Int
)

@Entity(tableName = "week_notes")
data class WeekNote(
    @PrimaryKey val date: String, // yyyy-MM-dd
    val text: String = ""
)

@Entity(tableName = "habits")
data class Habit(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val position: Int
)

@Entity(tableName = "habit_entries")
data class HabitEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val habitId: Long,
    val date: String, // yyyy-MM-dd
    val completed: Boolean = false
)