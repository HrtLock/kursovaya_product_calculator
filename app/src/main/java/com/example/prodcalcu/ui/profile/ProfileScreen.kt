package com.example.prodcalcu.ui.profile

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.NavHostController
import com.example.prodcalcu.R // Импортируйте ваш ресурс изображения здесь.
import com.example.prodcalcu.ui.navigation.BottomNavigationBar
import com.google.firebase.Firebase
import com.google.firebase.auth.auth


@Composable
fun ProfileScreen(navController: NavHostController) {
    val auth = Firebase.auth
    val context = LocalContext.current

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                currentPage = "profile", // Указываем текущую страницу
                onNavigate = { destination -> navController.navigate(destination) }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.White)
        ) {
            // Фоновое изображение
            Image(
                painter = painterResource(id = R.drawable.back_white), // Замените на ваш ресурс изображения.
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 70.dp)
                    .padding(top = 70.dp)

            )

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Заголовок профиля
                Text(
                    text = "ПРОФИЛЬ",
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                        .padding(top = 16.dp)
                )

                // Строка с почтой и кнопкой выхода
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Здесь замените "user@example.com" на почту текущего пользователя.
                    Text(text = "Почта: ${auth.currentUser?.email.toString()}", style = MaterialTheme.typography.bodyLarge)

                    Text(
                        text = "Выйти",
                        color = colorResource(R.color.primaryDark),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.clickable {
                            auth.signOut()
                            navController.navigate("login") // Замените на ваш маршрут логина.
                        }
                    )
                }

                Spacer(modifier = Modifier.weight(1f))
                // Карточка с поддержкой внизу экрана
                Card(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardColors(
                        containerColor = colorResource(R.color.white),
                        contentColor = colorResource(R.color.transparent),
                        disabledContainerColor = colorResource(R.color.transparent),
                        disabledContentColor = colorResource(R.color.transparent),
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "Если у вас возникла проблема,\nнапишите в поддержку",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 8.dp),
                            color = colorResource(R.color.black)
                        )

                        Text(
                            text = "support@teamarctic.ru",
                            style = MaterialTheme.typography.bodyLarge.copy(color = colorResource(R.color.primaryDark)),
                            modifier = Modifier.clickable {
                                val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                                    data = Uri.parse("mailto:support@teamarctic.ru")
                                }
                                context.startActivity(emailIntent)
                            }
                        )
                    }
                }
            }
        }
    }
}