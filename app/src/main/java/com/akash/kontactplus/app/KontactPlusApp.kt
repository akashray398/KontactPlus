package com.akash.kontactplus.app

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.akash.kontactplus.navigation.KontactNavHost

@Composable
fun KontactPlusApp(
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()

    KontactNavHost(
        navController = navController,
        modifier = modifier,
    )
}
