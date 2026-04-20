package com.example.toollink

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.toollink.ui.screens.BookingHistoryScreen
import com.example.toollink.ui.screens.PaymentScreen
import com.example.toollink.ui.theme.ToolLinkTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ToolLinkTheme {
                var currentScreen by remember { mutableStateOf("history") }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    when (currentScreen) {
                        "history" -> {
                            Scaffold(
                                floatingActionButton = {
                                    ExtendedFloatingActionButton(
                                        onClick = { currentScreen = "payment" },
                                        icon = { Icon(Icons.Filled.Add, contentDescription = "Add") },
                                        text = { Text("Mock Payment") }
                                    )
                                }
                            ) { padding ->
                                Box(modifier = Modifier.padding(padding)) {
                                    BookingHistoryScreen()
                                }
                            }
                        }
                        "payment" -> {
                            PaymentScreen(onPaymentSuccess = {
                                currentScreen = "history"
                            })
                        }
                    }
                }
            }
        }
    }
}
