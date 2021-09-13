package com.attafitamim.nfc.view.destinations.cards.scan.view

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.attafitamim.nfc.view.destinations.cards.list.view.BankCardsListWidget
import com.attafitamim.nfc.view.destinations.cards.scan.model.BankCardScanViewModel
import com.attafitamim.nfc.view.navigation.NavigationDestination
import org.koin.androidx.compose.get
import org.koin.androidx.compose.getViewModel

class BankCardScanDestination : NavigationDestination() {

    @Composable
    override fun Present(navController: NavHostController) {
        BankCardScanWidget(
            viewModel = getViewModel(),
            navController = navController
        )
    }
}