package com.example.joiefull

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.joiefull.presentation.navigation.NavGraph
import com.example.joiefull.ui.theme.JoiefullTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen() // ← doit être AVANT super.onCreate
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JoiefullTheme {
                NavGraph(modifier = Modifier.fillMaxSize())
            }
        }
    }
}