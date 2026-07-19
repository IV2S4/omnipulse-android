package com.omnipulse.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.omnipulse.app.ui.OmniPulseApp
import com.omnipulse.app.ui.theme.OmniPulseTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OmniPulseTheme {
                OmniPulseApp()
            }
        }
    }
}
