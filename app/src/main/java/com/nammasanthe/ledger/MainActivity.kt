package com.nammasanthe.ledger

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.nammasanthe.ledger.presentation.NammaSantheLedgerApp
import com.nammasanthe.ledger.ui.theme.NammaSantheLedgerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            NammaSantheLedgerTheme {
                NammaSantheLedgerApp()
            }
        }
    }
}
