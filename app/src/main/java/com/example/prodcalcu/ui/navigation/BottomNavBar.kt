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
import androidx.compose.ui.unit.dp

@Composable
fun BottomNavigationBar(
    currentPage: String,
    onNavigate: (String) -> Unit
) {
    BottomAppBar(
        content = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Иконка для экрана конструктора
                IconButton(
                    onClick = { onNavigate("calculator") },
                    modifier = Modifier
                        .then(
                            if (currentPage == "calculator") Modifier.background(MaterialTheme.colorScheme.primary) else Modifier
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Конструктор",
                        tint = if (currentPage == "calculator") MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground
                    )
                }

                // Иконка для экрана списка раскладок
                IconButton(
                    onClick = { onNavigate("list") },
                    modifier = Modifier
                        .then(
                            if (currentPage == "list") Modifier.background(MaterialTheme.colorScheme.primary) else Modifier
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.List,
                        contentDescription = "Раскладки",
                        tint = if (currentPage == "list") MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground
                    )
                }

                // Иконка для экрана профиля
                IconButton(
                    onClick = { onNavigate("profile") },
                    modifier = Modifier
                        .then(
                            if (currentPage == "profile") Modifier.background(MaterialTheme.colorScheme.primary) else Modifier
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Профиль",
                        tint = if (currentPage == "profile") MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        },
        modifier = Modifier.height(100.dp)
    )
}