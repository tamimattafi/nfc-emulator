package com.attafitamim.nfc.view.destinations.cards.list.view

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.attafitamim.nfc.view.navigation.NavigationDestination
import org.koin.androidx.compose.get
import org.koin.androidx.compose.getViewModel

class BankCardsListDestination : NavigationDestination() {

    @Composable
    override fun Present(navController: NavHostController) {
        BankCardsListWidget(
            viewModel = getViewModel(),
            navController = navController
        )
    }
}