package com.goodplan.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.goodplan.app.GoodPlanApp
import com.goodplan.app.data.entity.Task
import com.goodplan.app.data.entity.WeekGoal
import com.goodplan.app.data.entity.WeekNote
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters

class WeekViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = (app as GoodPlanApp).repository
    private val fmt = DateTimeFormatter.ISO_LOCAL_DATE

    private val _weekStart = MutableStateFlow(currentMonday())
    val weekStart: StateFlow<LocalDate> = _weekStart.asStateFlow()

    private val _selectedDay = MutableStateFlow(LocalDate.now())
    val selectedDay: StateFlow<LocalDate> = _selectedDay.asStateFlow()

    val weekDays: StateFlow<List<LocalDate>> = _weekStart.map { start ->
        (0..6).map { start.plusDays(it.toLong()) }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val weekGoal: StateFlow<WeekGoal?> = _weekStart.flatMapLatest {
        repo.weekGoal(it.format(fmt))
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val tasksForSelected: StateFlow<List<Task>> = _selectedDay.flatMapLatest {
        repo.tasksForDate(it.format(fmt))
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val weekTasks: StateFlow<List<Task>> = _weekStart.flatMapLatest { start ->
        repo.tasksForWeek(start.format(fmt), start.plusDays(6).format(fmt))
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val noteForSelected: StateFlow<WeekNote?> = _selectedDay.flatMapLatest {
        repo.note(it.format(fmt))
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    fun setWeekStart(date: LocalDate) {
        val monday = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        _weekStart.value = monday
    }

    fun selectDay(date: LocalDate) { _selectedDay.value = date }

    fun saveGoals(g1: String, g2: String, g3: String, main: String) {
        viewModelScope.launch {
            repo.saveWeekGoal(WeekGoal(
                weekStart = _weekStart.value.format(fmt),
                goal1 = g1, goal2 = g2, goal3 = g3, mainResult = main
            ))
        }
    }

    fun addTask(text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            val current = tasksForSelected.value
            repo.addTask(Task(
                date = _selectedDay.value.format(fmt),
                text = text,
                position = current.size
            ))
        }
    }

    fun toggleTask(task: Task) {
        viewModelScope.launch { repo.updateTask(task.copy(isCompleted = !task.isCompleted)) }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch { repo.updateTask(task) }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch { repo.deleteTask(task) }
    }

    fun saveNote(text: String) {
        viewModelScope.launch {
            repo.saveNote(WeekNote(date = _selectedDay.value.format(fmt), text = text))
        }
    }

    // Аналитика недели
    data class WeekStats(
        val total: Int,
        val done: Int,
        val avgProgress: Float,
        val bestDay: LocalDate?,
        val activeDays: Int
    )

    val stats: StateFlow<WeekStats> = weekTasks.combine(weekDays) { tasks, days ->
        val total = tasks.size
        val done = tasks.count { it.isCompleted }
        val perDay = days.map { d ->
            val dayTasks = tasks.filter { it.date == d.format(fmt) }
            if (dayTasks.isEmpty()) 0f
            else dayTasks.count { it.isCompleted }.toFloat() / dayTasks.size
        }
        val avg = if (perDay.isEmpty()) 0f else perDay.average().toFloat()
        val bestIdx = perDay.indexOf(perDay.maxOrNull() ?: 0f)
        val bestDay = if ((perDay.maxOrNull() ?: 0f) > 0f && bestIdx >= 0) days.getOrNull(bestIdx) else null
        val active = perDay.count { it > 0f }
        WeekStats(total, done, avg, bestDay, active)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, WeekStats(0, 0, 0f, null, 0))

    fun dayProgress(date: LocalDate): Float {
        val tasks = weekTasks.value.filter { it.date == date.format(fmt) }
        if (tasks.isEmpty()) return 0f
        return tasks.count { it.isCompleted }.toFloat() / tasks.size
    }

    private fun currentMonday(): LocalDate =
        LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
}