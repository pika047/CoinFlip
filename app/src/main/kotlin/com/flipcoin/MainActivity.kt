package com.flipcoin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.flipcoin.core.designsystem.theme.FlipCoinTheme
import com.flipcoin.ui.FlipCoinApp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            FlipCoinTheme {
                FlipCoinApp()
            }
        }
    }
}
