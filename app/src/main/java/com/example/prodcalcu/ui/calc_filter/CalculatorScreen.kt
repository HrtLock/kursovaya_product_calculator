@file:OptIn(ExperimentalLayoutApi::class)

package com.example.prodcalcu.ui.calc_filter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.prodcalcu.R
import com.example.prodcalcu.logic.CampType
import com.example.prodcalcu.logic.Climate
import com.example.prodcalcu.ui.navigation.BottomNavigationBar
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


@Composable
fun CalculatorScreen(navController: NavHostController) {
    // Хранимые значения
    val state = rememberScrollState()

    var calcName by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    var dayNumber by remember { mutableStateOf(1) }
    var personNumber by remember { mutableStateOf(1) }
    var selectedActivity by remember { mutableStateOf(CampType.PERMANENT_CAMP) }
    var selectedSeason by remember { mutableStateOf(Climate.SUMMER) }

    // Список фильтров
    val activities = listOf(CampType.PERMANENT_CAMP, CampType.MOBILE_CAMP, CampType.RADIAL_EXCURSIONS)
    val seasons = listOf(Climate.SUMMER, Climate.WINTER, Climate.DEMISEASON)



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
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardColors(
                    containerColor = colorResource(R.color.white),
                    contentColor = colorResource(R.color.black),
                    disabledContainerColor = colorResource(R.color.transparent),
                    disabledContentColor = colorResource(R.color.transparent),
                )

            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    // Заголовок
                    Text(
                        text = "НАСТРОИТЬ ПРОДУКТОВУЮ РАСКЛАДКУ",
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        value = calcName,
                        onValueChange = { newValue ->
                            if (newValue.length <= 33) {
                                calcName = newValue
                                errorMessage = ""
                            } else {
                                errorMessage = "Максимальная длина 33 символа"
                            }
                        },
                        label = { Text("Название раскладки") },
                        shape = RoundedCornerShape(50.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = colorResource(R.color.grey),
                            unfocusedContainerColor = colorResource(R.color.grey),
                            focusedIndicatorColor = colorResource(R.color.transparent),
                            errorIndicatorColor = colorResource(R.color.transparent),
                            unfocusedIndicatorColor = colorResource(R.color.transparent),
                            focusedLabelColor = colorResource(R.color.primaryDark),
                            cursorColor = colorResource(R.color.primaryDark),
                        ),
                        isError = errorMessage.isNotEmpty()
                    )
                    if (errorMessage.isNotEmpty()) {
                        Text(
                            text = errorMessage,
                            color = Color.Red,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
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
                    HorizontalDivider(
                        modifier = Modifier.fillMaxWidth(),
                        color = colorResource(R.color.grey),
                        thickness = 1.dp // Устанавливает толщину линии
                    )
                    // Выбор активности (Choice Chips)
                    Text("Активность", style = MaterialTheme.typography.bodyLarge)
                    FlowRow(
                        modifier = Modifier,
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        activities.forEach { activity ->
                            FilterChip(
                                modifier = Modifier.padding(horizontal = 2.dp),
                                selected = selectedActivity == activity,
                                onClick = { selectedActivity = activity },
                                label = { Text(activity.displayName) },
                                shape = RoundedCornerShape(50.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    labelColor = colorResource(R.color.black),
                                    selectedLabelColor = colorResource(R.color.white),
                                    selectedContainerColor = colorResource(R.color.primaryLight),
                                    disabledContainerColor = colorResource(R.color.grey),
                                    containerColor = colorResource(R.color.grey),
                                    disabledLabelColor = colorResource(R.color.black)
                                ),
                                border = null
                            )
                        }
                    }
                    HorizontalDivider(
                        modifier = Modifier.fillMaxWidth(),
                        color = colorResource(R.color.grey),
                        thickness = 1.dp // Устанавливает толщину линии
                    )
                    // Время года / Климат (Choice Chips)
                    Text("Время года / Климат", style = MaterialTheme.typography.bodyLarge)
                    FlowRow(
                        modifier = Modifier,
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        seasons.forEach { season ->
                            FilterChip(
                                modifier = Modifier.padding(horizontal = 2.dp),
                                selected = selectedSeason == season,
                                onClick = { selectedSeason = season },
                                label = { Text(season.displayName) },
                                shape = RoundedCornerShape(50.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    labelColor = colorResource(R.color.black),
                                    selectedLabelColor = colorResource(R.color.white),
                                    selectedContainerColor = colorResource(R.color.primaryLight),
                                    disabledContainerColor = colorResource(R.color.grey),
                                    containerColor = colorResource(R.color.grey),
                                    disabledLabelColor = colorResource(R.color.black)
                                ),
                                border = null
                            )
                        }
                    }


                }
            }
            // Кнопка Применить фильтр
            Button(
                modifier = Modifier.fillMaxWidth(0.5f).align(Alignment.CenterHorizontally),
                colors = ButtonDefaults.buttonColors(
                    colorResource(R.color.primaryDark),
                ),
                onClick = {
                    if (calcName == "") {
                        errorMessage = "Заполните название раскладки"

                    } else {
                        navController.navigate("result?calcName=$calcName&dayNumber=$dayNumber&personNumber=$personNumber&activity=${selectedActivity.name}&season=${selectedSeason.name}")
                    }
                },
            ) {
                Text(text = "Применить фильтр")
            }
        }
    }
}

@Composable
fun NumberPicker(label: String, value: Int, onValueChange: (Int) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
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




