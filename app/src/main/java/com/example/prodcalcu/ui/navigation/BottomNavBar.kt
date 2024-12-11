package com.example.prodcalcu.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.prodcalcu.R

@Composable
fun BottomNavigationBar(
    currentPage: String,
    onNavigate: (String) -> Unit
) {
    BottomAppBar(
        containerColor = colorResource(R.color.primaryLight),
        modifier = Modifier
            .height(100.dp)
            .background(colorResource(R.color.primaryLight))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Иконка для экрана конструктора
            IconButton(
                onClick = { onNavigate("calculator") },
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.edit_square),
                    contentDescription = "Конструктор",
                    tint = if (currentPage == "calculator") colorResource(R.color.white) else colorResource(R.color.secondaryLight)
                )
            }

            // Иконка для экрана списка раскладок
            IconButton(
                onClick = { onNavigate("list") },
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.list_alt),
                    contentDescription = "Раскладки",
                    tint = if (currentPage == "list") colorResource(R.color.white) else colorResource(R.color.secondaryLight)
                )
            }

            // Иконка для экрана профиля
            IconButton(
                onClick = { onNavigate("profile") },
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.account_circle),
                    contentDescription = "Профиль",
                    tint = if (currentPage == "profile") colorResource(R.color.white) else colorResource(R.color.secondaryLight)
                )
            }
        }
    }
}
