package com.carlo_mederos.room

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.carlo_mederos.room.ui.TareaScreen
import com.carlo_mederos.room.ui.theme.RoomTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RoomTheme {
                TareaScreen()
            }
        }
    }
}
