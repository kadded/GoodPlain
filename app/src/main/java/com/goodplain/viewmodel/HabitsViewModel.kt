package com.goodplan.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.goodplan.app.GoodPlanApp
import com.goodplan.app.data.entity.Habit
import com.goodplan.app.data.entity.HabitEntry
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

class HabitsViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = (app as GoodPlanApp).repository
    private val fmt = DateTimeFormatter.ISO_LOCAL_DATE

    private val now = LocalDate.now()
    private val _month = MutableStateFlow(YearMonth.of(now.year, now.monthValue))
    val month: StateFlow<YearMonth> = _month.asStateFlow()

    val daysInMonth: StateFlow<List<Int?>> = _month.map { ym ->
        val len = ym.lengthOfMonth()
        (1..31).map { if (it <= len) it else null }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val habits: StateFlow<List<Habit>> = repo.habits()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val entries: StateFlow<List<HabitEntry>> = _month.flatMapLatest { ym ->
        val from = ym.atDay(1).format(fmt)
        val to = ym.atEndOfMonth().format(fmt)
        repo.allEntriesInRange(from, to)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun setMonth(year: Int, month: Int) {
        _month.value = YearMonth.of(year, month.coerceIn(1, 12))
    }

    fun addHabit(name: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            val pos = habits.value.size
            repo.addHabit(Habit(name = name, position = pos))
        }
    }

    fun updateHabit(h: Habit, newName: String) {
        viewModelScope.launch { repo.updateHabit(h.copy(name = newName)) }
    }

    fun deleteHabit(h: Habit) {
        viewModelScope.launch { repo.deleteHabit(h) }
    }

    fun toggle(habitId: Long, day: Int) {
        val date = _month.value.atDay(day).format(fmt)
        val exists = entries.value.any { it.habitId == habitId && it.date == date }
        viewModelScope.launch { repo.toggleEntry(habitId, date, !exists) }
    }

    fun isCompleted(habitId: Long, day: Int): Boolean {
        val date = _month.value.atDay(day).format(fmt)
        return entries.value.any { it.habitId == habitId && it.date == date }
    }

    fun completionPercent(habitId: Long): Int {
        val total = _month.value.lengthOfMonth()
        if (total == 0) return 0
        val done = entries.value.count { it.habitId == habitId }
        return (done * 100 / total).coerceIn(0, 100)
    }

    fun ratingStars(percent: Int): Int = when {
        percent >= 90 -> 5
        percent >= 70 -> 4
        percent >= 50 -> 3
        percent >= 30 -> 2
        percent > 0 -> 1
        else -> 0
    }

    fun totalForDay(day: Int): Int {
        val ym = _month.value
        val date = ym.atDay(day).format(fmt)
        return entries.value.count { it.date == date }
    }
}