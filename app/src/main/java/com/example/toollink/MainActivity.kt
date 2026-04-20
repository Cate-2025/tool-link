package com.example.toollink

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import com.example.toollink.ui.home.HomeScreen
import com.example.toollink.ui.owner.OwnerRegistrationScreen
import com.example.toollink.ui.theme.ToolLinkTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ToolLinkTheme {
                var currentScreen by remember { mutableStateOf("home") }

                when (currentScreen) {
                    "home" -> HomeScreen(
                        onNavigateToRegistration = { currentScreen = "registration" }
                    )
                    "registration" -> OwnerRegistrationScreen()
                }
            }
        }
    }
}
