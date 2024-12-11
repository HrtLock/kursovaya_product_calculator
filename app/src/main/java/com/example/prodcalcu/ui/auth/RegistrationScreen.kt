package com.example.prodcalcu.ui.register

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.prodcalcu.R
import com.google.firebase.auth.FirebaseAuth

@Composable
fun RegistrationScreen(navController: NavHostController) {
    val auth = FirebaseAuth.getInstance()

    val emailState = remember { mutableStateOf("") }
    val passwordState = remember { mutableStateOf("") }
    val confirmPasswordState = remember { mutableStateOf("") }
    val errorMessage = remember { mutableStateOf("") }

    Scaffold() { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween // Для равномерного распределения элементов.
        ) {
            // Верхнее изображение
            Image(
                painter = painterResource(id = R.drawable.app_logo),
                contentDescription = "Top Image",
                modifier = Modifier.fillMaxWidth(0.65f).padding(top = 32.dp),
                colorFilter = ColorFilter.tint(colorResource(R.color.primaryDark)),
                alignment = Alignment.Center
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f) // Позволяет этому Column занимать оставшееся пространство.
            ) {
                TextField(
                    modifier = Modifier.fillMaxWidth(0.8f).height(56.dp),
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
                    modifier = Modifier.fillMaxWidth(0.8f).height(56.dp),
                    value = passwordState.value,
                    onValueChange = { passwordState.value = it },
                    label = { Text("Пароль") },
                    visualTransformation = PasswordVisualTransformation(),
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
                    modifier = Modifier.fillMaxWidth(0.8f).height(56.dp),
                    value = confirmPasswordState.value,
                    onValueChange = { confirmPasswordState.value = it },
                    label = { Text("Подтвердите пароль") },
                    visualTransformation = PasswordVisualTransformation(),
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

                Spacer(modifier = Modifier.height(18.dp))

                if (errorMessage.value.isNotEmpty()) {
                    Text(text = errorMessage.value, color = Color.Red)
                    Spacer(modifier = Modifier.height(10.dp))
                }

                Button(
                    modifier = Modifier.fillMaxWidth(0.5f),
                    colors = ButtonDefaults.buttonColors(colorResource(R.color.primaryDark)),
                    onClick = {
                        validateAndSignUp(navController, auth, emailState.value, passwordState.value, confirmPasswordState.value, errorMessage)
                    }) {
                    Text(text = "Зарегистрироваться")
                }

                Button(
                    modifier = Modifier.fillMaxWidth(0.5f),
                    colors = ButtonDefaults.buttonColors(colorResource(R.color.primaryLight)),
                    onClick = {
                        navController.navigate("login")
                    }) {
                    Text(color = colorResource(R.color.white), text = "Назад к входу")
                }
            }

            // Нижнее изображение
            Image(
                painter = painterResource(id = R.drawable.logo), // Замените на ваше нижнее изображение
                contentDescription = "Bottom Image",
                modifier = Modifier.fillMaxWidth(0.6f).padding(bottom=32.dp),
                colorFilter= ColorFilter.tint(colorResource(R.color.primaryDark)),
                alignment= Alignment.Center
            )
        }
    }
}

private fun validateAndSignUp(navController: NavHostController, auth: FirebaseAuth, email: String, password: String, confirmPassword: String, errorMessage: MutableState<String>) {
    if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
        errorMessage.value= "Пожалуйста, заполните все поля."
        return
    }

    if (password != confirmPassword) {
        errorMessage.value= "Пароли не совпадают."
        return
    }

    auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("MyLog", "Sign Up successful!")
                navController.navigate("calculator")
            } else {
                errorMessage.value= "Ошибка регистрации. Попробуйте еще раз."
                Log.d("MyLog", "Sign Up failure!")
            }
        }
}
