package com.example.prodcalcu.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.prodcalcu.R
import com.example.prodcalcu.ui.navigation.BottomNavigationBar
import com.google.firebase.auth.FirebaseAuth

@Composable
fun IntroScreen(navController: NavHostController) {
    val auth = FirebaseAuth.getInstance()
    if (auth.currentUser != null) {
        navController.navigate("calculator")
    }
    else {
        navController.navigate("login")
    }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.app_logo),
                contentDescription = "Top Image",
                modifier = Modifier.fillMaxWidth(0.9f),
                colorFilter = ColorFilter.tint(colorResource(R.color.primaryDark))
            )
        }
    }
}