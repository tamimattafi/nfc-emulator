package com.attafitamim.nfc.view.destinations.cards.list.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.attafitamim.nfc.view.destinations.cards.list.model.BankCardsListSideEffect
import com.attafitamim.nfc.view.destinations.cards.list.model.BankCardsListViewModel
import com.attafitamim.nfc.view.navigation.NavigationDestination.Companion.toDestination
import kotlinx.coroutines.flow.collect

@Composable
fun BankCardsListWidget(
    viewModel: BankCardsListViewModel,
    navController: NavHostController
) {
    LaunchedEffect(viewModel) {
        viewModel.container.sideEffectFlow.collect { sideEffect ->
            when (sideEffect) {
                is BankCardsListSideEffect.OpenDestination -> {
                    navController.toDestination(sideEffect.destination)
                }
            }
        }
    }
}