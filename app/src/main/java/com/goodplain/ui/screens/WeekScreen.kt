package com.goodplan.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.goodplan.app.data.entity.Task
import com.goodplan.app.ui.components.RingProgress
import com.goodplan.app.viewmodel.WeekViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeekScreen(
    vm: WeekViewModel,
    onOpenInstruction: () -> Unit
) {
    val weekStart by vm.weekStart.collectAsState()
    val selectedDay by vm.selectedDay.collectAsState()
    val weekDays by vm.weekDays.collectAsState()
    val weekGoal by vm.weekGoal.collectAsState()
    val tasks by vm.tasksForSelected.collectAsState()
    val note by vm.noteForSelected.collectAsState()

    var taskInput by remember { mutableStateOf("") }
    var noteInput by remember(selectedDay, note) { mutableStateOf(note?.text ?: "") }
    var showGoalDialog by remember { mutableStateOf(false) }

    val ruMonthFormatter = DateTimeFormatter.ofPattern("d MMMM", Locale("ru"))
    val dayNameFormatter = DateTimeFormatter.ofPattern("EEEE", Locale("ru"))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Заголовок недели
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 1.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { vm.setWeekStart(weekStart.minusWeeks(1)) }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Пред. неделя")
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Неделя ${weekStart.format(ruMonthFormatter)} — ${weekStart.plusDays(6).format(ruMonthFormatter)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "GoodPlan",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(onClick = { vm.setWeekStart(weekStart.plusWeeks(1)) }) {
                    Icon(Icons.Default.ArrowForward, contentDescription = "След. неделя")
                }
            }
        }

        // 7 дней недели с кольцевым прогрессом
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            val ruShortDays = listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс")
            weekDays.forEachIndexed { index, date ->
                val isSelected = date == selectedDay
                val progress = vm.dayProgress(date)

                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
                        .clickable { vm.selectDay(date) }
                        .padding(vertical = 8.dp, horizontal = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = ruShortDays.getOrElse(index) { "" },
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray
                    )
                    Text(
                        text = date.dayOfMonth.toString(),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    RingProgress(
                        progress = progress,
                        size = 28.dp,
                        strokeWidth = 3.dp,
                        progressColor = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Карточка 3 главных целей недели
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "🎯 Фокус недели (3 победы)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            IconButton(onClick = { showGoalDialog = true }) {
                                Icon(Icons.Default.Edit, contentDescription = "Редактировать цели", modifier = Modifier.size(18.dp))
                            }
                        }

                        if (!weekGoal?.mainResult.isNullOrBlank()) {
                            Text(
                                text = "Главный результат: ${weekGoal?.mainResult}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                        }

                        listOf(weekGoal?.goal1, weekGoal?.goal2, weekGoal?.goal3).forEachIndexed { i, g ->
                            if (!g.isNullOrBlank()) {
                                Text(
                                    text = "${i + 1}. $g",
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(vertical = 2.dp)
                                )
                            }
                        }
                        if (weekGoal?.goal1.isNullOrBlank() && weekGoal?.goal2.isNullOrBlank() && weekGoal?.goal3.isNullOrBlank()) {
                            Text(
                                text = "Нажмите карандаш, чтобы задать 3 фокуса недели",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }

            // Выбранный день и поле ввода задачи
            item {
                Text(
                    text = "${selectedDay.format(dayNameFormatter).replaceFirstChar { it.uppercase() }}, ${selectedDay.format(ruMonthFormatter)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = taskInput,
                        onValueChange = { taskInput = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Новая задача...") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (taskInput.isNotBlank()) {
                                vm.addTask(taskInput.trim())
                                taskInput = ""
                            }
                        },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Добавить")
                    }
                }
            }

            // Список задач выбранного дня
            items(tasks, key = { it.id }) { task ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = task.isCompleted,
                            onCheckedChange = { vm.toggleTask(task) }
                        )
                        Text(
                            text = task.text,
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                            ),
                            color = if (task.isCompleted) Color.Gray else Color.Black
                        )
                        // Перенос на завтра
                        if (!task.isCompleted) {
                            IconButton(onClick = {
                                vm.updateTask(task.copy(date = selectedDay.plusDays(1).toString()))
                            }) {
                                Icon(Icons.Default.ArrowForward, contentDescription = "На завтра", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                            }
                        }
                        IconButton(onClick = { vm.deleteTask(task) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Удалить", tint = Color.LightGray, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }

            // Заметка / Рефлексия дня
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "💡 Заметка и рефлексия дня",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = noteInput,
                            onValueChange = {
                                noteInput = it
                                vm.saveNote(it)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Мысли дня, инсайты, благодарность...") },
                            minLines = 3,
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }
        }
    }

    // Диалог редактирования 3 целей недели
    if (showGoalDialog) {
        var g1 by remember { mutableStateOf(weekGoal?.goal1 ?: "") }
        var g2 by remember { mutableStateOf(weekGoal?.goal2 ?: "") }
        var g3 by remember { mutableStateOf(weekGoal?.goal3 ?: "") }
        var main by remember { mutableStateOf(weekGoal?.mainResult ?: "") }

        AlertDialog(
            onDismissRequest = { showGoalDialog = false },
            title = { Text("Цели недели (Правило 3)") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = main, onValueChange = { main = it }, label = { Text("Главный результат") })
                    OutlinedTextField(value = g1, onValueChange = { g1 = it }, label = { Text("Цель №1") })
                    OutlinedTextField(value = g2, onValueChange = { g2 = it }, label = { Text("Цель №2") })
                    OutlinedTextField(value = g3, onValueChange = { g3 = it }, label = { Text("Цель №3") })
                }
            },
            confirmButton = {
                Button(onClick = {
                    vm.saveGoals(g1, g2, g3, main)
                    showGoalDialog = false
                }) {
                    Text("Сохранить")
                }
            },
            dismissButton = {
                TextButton(onClick = { showGoalDialog = false }) { Text("Отмена") }
            }
        )
    }
}