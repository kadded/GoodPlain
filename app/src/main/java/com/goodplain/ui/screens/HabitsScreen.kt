package com.goodplan.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.goodplan.app.viewmodel.HabitsViewModel
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun HabitsScreen(vm: HabitsViewModel) {
    val month by vm.month.collectAsState()
    val habits by vm.habits.collectAsState()
    val daysInMonth by vm.daysInMonth.collectAsState()
    val entries by vm.entries.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var habitTitle by remember { mutableStateOf("") }

    val horizontalScrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Навигация по месяцу
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
                IconButton(onClick = {
                    val prev = month.minusMonths(1)
                    vm.setMonth(prev.year, prev.monthValue)
                }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Пред. месяц")
                }

                Text(
                    text = "${month.month.getDisplayName(TextStyle.FULL_STANDALONE, Locale("ru")).replaceFirstChar { it.uppercase() }} ${month.year}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                IconButton(onClick = {
                    val next = month.plusMonths(1)
                    vm.setMonth(next.year, next.monthValue)
                }) {
                    Icon(Icons.Default.ArrowForward, contentDescription = "След. месяц")
                }
            }
        }

        // Кнопка добавления привычки
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Матрица привычек",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Button(
                onClick = { showAddDialog = true },
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Новая")
            }
        }

        // Сетка трекера (горизонтальный скролл матрицы)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .horizontalScroll(horizontalScrollState)
        ) {
            LazyColumn(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Заголовок таблицы с днями
                item {
                    Row(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Привычка",
                            modifier = Modifier.width(160.dp).padding(start = 8.dp),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )

                        daysInMonth.filterNotNull().forEach { day ->
                            Text(
                                text = day.toString(),
                                modifier = Modifier.width(32.dp),
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = "%",
                            modifier = Modifier.width(44.dp),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Строки привычек
                items(habits, key = { it.id }) { habit ->
                    val percent = vm.completionPercent(habit.id)

                    Card(
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = habit.name,
                                modifier = Modifier
                                    .width(160.dp)
                                    .padding(start = 8.dp),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium
                            )

                            daysInMonth.filterNotNull().forEach { day ->
                                val isDone = vm.isCompleted(habit.id, day)
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .padding(3.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(
                                            if (isDone) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                                        )
                                        .clickable { vm.toggle(habit.id, day) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isDone) {
                                        Text("✓", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            Text(
                                text = "$percent%",
                                modifier = Modifier.width(44.dp),
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Новая привычка") },
            text = {
                OutlinedTextField(
                    value = habitTitle,
                    onValueChange = { habitTitle = it },
                    label = { Text("Название (напр. Зарядка, 2L воды)") },
                    singleLine = true
                )
            },
            confirmButton = {
                Button(onClick = {
                    if (habitTitle.isNotBlank()) {
                        vm.addHabit(habitTitle.trim())
                        habitTitle = ""
                        showAddDialog = false
                    }
                }) {
                    Text("Создать")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("Отмена") }
            }
        )
    }
}