@file:OptIn(ExperimentalLayoutApi::class)

package com.example.prodcalcu.ui.calc_filter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.prodcalcu.ui.navigation.BottomNavigationBar


@Composable
fun CalculatorScreen(navController: NavHostController) {
    // Хранимые значения
    val state = rememberScrollState()

    var calcName by remember { mutableStateOf("") }
    var dayNumber by remember { mutableStateOf(1) }
    var personNumber by remember { mutableStateOf(1) }
    var selectedActivity by remember { mutableStateOf("") }
    var selectedSeason by remember { mutableStateOf("") }
    var selectedConditions by remember { mutableStateOf(listOf<String>()) }

    // Список фильтров
    val activities = listOf("Лагерь", "Радиальная", "Подвижный лагерь")
    val seasons = listOf("Зима", "Лето", "Демисезон")
    val conditions = listOf("Горы", "Лесополоса", "Нет дерева", "Нет питьевой воды", "Многодневный переход")

    Scaffold (
        bottomBar = {
            BottomNavigationBar(
                currentPage = "calculator", // Указываем текущую страницу
                onNavigate = { destination -> navController.navigate(destination) }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(state)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Заголовок
                    Text(
                        text = "НАСТРОИТЬ ПРОДУКТОВУЮ РАСКЛАДКУ",
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Поле ввода названия раскладки
                    OutlinedTextField(
                        value = calcName,
                        onValueChange = {},
                        placeholder = { Text("Название раскладки") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Счетчики
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        NumberPicker(
                            label = "Количество дней",
                            value = dayNumber,
                            onValueChange = { dayNumber = it }
                        )
                        NumberPicker(
                            label = "Количество человек",
                            value = personNumber,
                            onValueChange = { personNumber = it }
                        )
                    }

                    // Выбор активности (Choice Chips)
                    Text("Активность", style = MaterialTheme.typography.bodyMedium)
                    FlowRow(modifier = Modifier.padding(8.dp)) {
                        activities.forEach { activity ->
                            FilterChip(
                                selected = selectedActivity == activity,
                                onClick = { selectedActivity = activity },
                                label = { Text(activity) }
                            )
                        }
                    }

                    // Время года / Климат (Choice Chips)
                    Text("Время года / Климат", style = MaterialTheme.typography.bodyMedium)
                    FlowRow(modifier = Modifier.padding(8.dp)) {
                        seasons.forEach { season ->
                            FilterChip(
                                selected = selectedSeason == season,
                                onClick = { selectedSeason = season },
                                label = { Text(season) }
                            )
                        }
                    }

                    // Условия (Filter Chips)
                    Text("Условия", style = MaterialTheme.typography.bodyMedium)
                    FlowRow(modifier = Modifier.padding(8.dp)) {
                        conditions.forEach { condition ->
                            FilterChip(
                                selected = selectedConditions.contains(condition),
                                onClick = {
                                    selectedConditions =
                                        if (selectedConditions.contains(condition)) {
                                            selectedConditions - condition
                                        } else {
                                            selectedConditions + condition
                                        }
                                },
                                label = { Text(condition) }
                            )
                        }
                    }
                }
            }
            // Кнопка Применить фильтр
            Button(
                onClick = {
                    val query = createFilterQuery(
                        calcName = calcName,
                        dayNumber = dayNumber,
                        personNumber = personNumber,
                        activity = selectedActivity,
                        season = selectedSeason,
                        conditions = selectedConditions
                    )
                    // Отправка запроса
                    println(query)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Применить фильтр")
            }
        }
    }
}

@Composable
fun NumberPicker(label: String, value: Int, onValueChange: (Int) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { if (value > 1) onValueChange(value - 1) }) {
                Icon(imageVector = Icons.Default.KeyboardArrowDown, contentDescription = "Minus")
            }
            Text(text = value.toString(), style = MaterialTheme.typography.bodyMedium)
            IconButton(onClick = { onValueChange(value + 1) }) {
                Icon(imageVector = Icons.Default.KeyboardArrowUp, contentDescription = "Plus")
            }
        }
    }
}

fun createFilterQuery(
    calcName: String,
    dayNumber: Int,
    personNumber: Int,
    activity: String,
    season: String,
    conditions: List<String>
): String {
    return "Название раскладки: $calcName, " +
            "Количество дней: $dayNumber, " +
            "Количество человек: $personNumber, " +
            "Активность: $activity, " +
            "Время года/Климат: $season, " +
            "Условия: ${conditions.joinToString(", ")}"
}


