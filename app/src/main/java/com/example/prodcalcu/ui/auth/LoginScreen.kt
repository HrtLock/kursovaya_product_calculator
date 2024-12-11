package com.example.prodcalcu.ui.auth

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.navigation.NavHostController
import com.example.prodcalcu.R

@Composable
fun LoginScreen(navController: NavHostController) {
    val auth = FirebaseAuth.getInstance()

    val emailState = remember { mutableStateOf("") }
    val passwordState = remember { mutableStateOf("") }
    val errorMessage = remember { mutableStateOf("") }

    if (auth.currentUser != null) {
        navController.navigate("calculator")
    }

    Scaffold() { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween // Изменяем на SpaceBetween для равномерного распределения элементов.
        ) {
            // Верхнее изображение
            Image(
                painter = painterResource(id = R.drawable.app_logo),
                contentDescription = "Bottom Image",
                modifier = Modifier
                    .fillMaxWidth(0.65f)
                    .padding(top = 32.dp),
                colorFilter = ColorFilter.tint(colorResource(R.color.primaryDark)),
                alignment = Alignment.Center
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f) // Позволяет этому Column занимать оставшееся пространство.
            ) {
                TextField(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(56.dp),
                    value = emailState.value,
                    onValueChange = { emailState.value = it },
                    label = { Text("Email") },
                    shape = RoundedCornerShape(50.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = colorResource(R.color.grey),
                        unfocusedContainerColor = colorResource(R.color.grey),
                        focusedIndicatorColor = colorResource(R.color.transparent),
                        unfocusedIndicatorColor = colorResource(R.color.transparent),
                        focusedLabelColor = colorResource(R.color.primaryDark),
                        cursorColor = colorResource(R.color.primaryDark),
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))

                TextField(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(56.dp),
                    value = passwordState.value,
                    onValueChange = { passwordState.value = it },
                    label = { Text("Пароль") },
                    visualTransformation = PasswordVisualTransformation(),
                    shape = RoundedCornerShape(50.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = colorResource(R.color.grey), // Цвет фона при фокусе
                        unfocusedContainerColor = colorResource(R.color.grey), // Цвет фона при отсутствии фокуса
                        focusedIndicatorColor = colorResource(R.color.transparent), // Убираем индикатор при фокусе
                        unfocusedIndicatorColor = colorResource(R.color.transparent), // Убираем индикатор при отсутствии фокуса
                        focusedLabelColor = colorResource(R.color.primaryDark),
                        cursorColor = colorResource(R.color.primaryDark),
                        )

                )

                Spacer(modifier = Modifier.height(18.dp))

                if (errorMessage.value.isNotEmpty()) {
                    Text(text = errorMessage.value, color = Color.Red)
                    Spacer(modifier = Modifier.height(10.dp))
                }

                Button(
                    modifier = Modifier.fillMaxWidth(0.5f),
                    colors = ButtonDefaults.buttonColors(
                        colorResource(R.color.primaryDark),
                    ),
                    onClick = {
                    validateAndSignIn(
                        auth,
                        emailState.value,
                        passwordState.value,
                        errorMessage,
                        navController
                    )
                }) {
                    Text(text = "Войти")
                }

                Button(
                    modifier = Modifier.fillMaxWidth(0.5f),
                    colors = ButtonDefaults.buttonColors(
                        colorResource(R.color.primaryLight),
                    ),

                    onClick = {
                    navController.navigate("register")
                }) {
                    Text(
                        color = colorResource(R.color.white),
                        text = "Создать аккаунт")
                }
            }

            // Нижнее изображение
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Bottom Image",
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .padding(bottom = 32.dp),
                colorFilter = ColorFilter.tint(colorResource(R.color.primaryDark)),
                alignment = Alignment.Center
            )
        }
    }
}

private fun validateAndSignIn(auth: FirebaseAuth, email: String, password: String, errorMessage: MutableState<String>, navController: NavHostController) {
    if (email.isEmpty() || password.isEmpty()) {
        errorMessage.value = "Пожалуйста, заполните все поля."
        return
    }

    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("MyLog", "Sign In successful!")
                navController.navigate("calculator") // Navigate to calculator screen.
            } else {
                errorMessage.value = "Некорректные данные. Проверьте почту и пароль."
                Log.d("MyLog", "Sign In failure!")
            }
        }
}
