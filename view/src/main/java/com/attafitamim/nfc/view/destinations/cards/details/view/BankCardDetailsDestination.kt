package com.attafitamim.nfc.view.destinations.cards.details.view

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.attafitamim.nfc.view.destinations.cards.details.model.BankCardDetailsViewModel
import com.attafitamim.nfc.view.navigation.NavigationDestination
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf

data class BankCardDetailsDestination(
    val cardId: Int
) : NavigationDestination() {

    @Composable
    override fun Present(navController: NavHostController) {
        val viewModel = getViewModel<BankCardDetailsViewModel> {
            parametersOf(this)
        }

        BankCardDetailsWidget(viewModel = viewModel)
    }
}