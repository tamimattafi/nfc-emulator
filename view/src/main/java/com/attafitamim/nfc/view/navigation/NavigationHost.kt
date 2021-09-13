package com.attafitamim.nfc.view.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.attafitamim.nfc.view.navigation.NavigationDestination.Companion.navigationDestination
import com.attafitamim.nfc.view.navigation.NavigationDestination.Companion.rawRoute

@Composable
fun NavigationHost(
    navController: NavHostController,
    initialDestination: Class<out NavigationDestination>,
    vararg destinations: Class<out NavigationDestination>
) {
    NavHost(
        navController = navController,
        startDestination = initialDestination.rawRoute,
    ) {
        destinations.forEach { destination ->
            navigationDestination(
                navController = navController,
                clazz = destination
            )
        }
    }
}