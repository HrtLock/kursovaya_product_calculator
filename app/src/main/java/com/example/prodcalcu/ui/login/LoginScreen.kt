package com.example.prodcalcu.ui.login

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.auth
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

@Composable
fun LoginScreen(navController: NavHostController) {

    val auth = Firebase.auth

    val emailState = remember {
        mutableStateOf("")
    }

    val passwordState = remember {
        mutableStateOf("")
    }

    if (auth.currentUser != null) {
        navController.navigate("calculator")
    }


    Column (modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        TextField(value = emailState.value, onValueChange = {
            emailState.value = it
        })
        Spacer(modifier = Modifier.height(10.dp))


        TextField(value = passwordState.value, onValueChange = {
            passwordState.value = it
        })
        Spacer(modifier = Modifier.height(10.dp))
        Button(onClick = {
            signIn(auth, emailState.value, passwordState.value)

        }) {
            Text(text = "Войти")
        }
        Button(onClick = {
            signUp(auth, emailState.value, passwordState.value)

        }) {
            Text(text = "Зарегистрироваться")
        }
    }
}

private fun signUp(auth: FirebaseAuth, email: String, password: String){
    auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener {
            if (it.isSuccessful){
                Log.d("MyLog", "Sign Up successful!")
            }
            else{
                Log.d("MyLog", "Sign Up failure!")
            }
        }
}

private fun signIn(auth: FirebaseAuth, email: String, password: String){
    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener {
            if (it.isSuccessful){
                Log.d("MyLog", "Sign In successful!")
            }
            else{
                Log.d("MyLog", "Sign In failure!")
            }
        }
}