package com.goodplan.app.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.goodplan.app.data.dao.HabitDao
import com.goodplan.app.data.dao.WeekDao
import com.goodplan.app.data.entity.*

@Database(
    entities = [WeekGoal::class, Task::class, WeekNote::class, Habit::class, HabitEntry::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun weekDao(): WeekDao
    abstract fun habitDao(): HabitDao
}