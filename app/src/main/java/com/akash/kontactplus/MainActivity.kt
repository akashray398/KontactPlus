package com.akash.kontactplus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.akash.kontactplus.app.KontactPlusApp
import com.akash.kontactplus.core.designsystem.theme.KontactPlusTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            KontactPlusTheme {
                KontactPlusApp()
            }
        }
    }
}
