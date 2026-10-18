package com.beyondthescreen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.beyondthescreen.ui.BeyondApp
import com.beyondthescreen.ui.BeyondTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BeyondTheme {
                BeyondApp()
            }
        }
    }
}
