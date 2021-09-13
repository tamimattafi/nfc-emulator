package com.attafitamim.nfc.view.destinations.cards.scan.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import com.attafitamim.nfc.view.destinations.cards.list.model.BankCardsListSideEffect
import com.attafitamim.nfc.view.destinations.cards.list.model.BankCardsListViewModel
import com.attafitamim.nfc.view.destinations.cards.scan.model.BankCardScanSideEffect
import com.attafitamim.nfc.view.destinations.cards.scan.model.BankCardScanViewModel
import com.attafitamim.nfc.view.navigation.NavigationDestination.Companion.toDestination
import kotlinx.coroutines.flow.collect

@Composable
fun BankCardScanWidget(
    viewModel: BankCardScanViewModel,
    navController: NavHostController
) {
    LaunchSideEffects(viewModel, navController)
}

@Composable
fun LaunchSideEffects(
    viewModel: BankCardScanViewModel,
    navController: NavHostController
) = LaunchedEffect(viewModel) {
    viewModel.container.sideEffectFlow.collect { sideEffect ->
        when (sideEffect) {
            is BankCardScanSideEffect.OpenDestination -> {
                navController.toDestination(sideEffect.destination)
            }
            BankCardScanSideEffect.ShowCardSavedToast -> TODO()
            is BankCardScanSideEffect.ShowScanErrorToast -> TODO()
        }
    }
}
