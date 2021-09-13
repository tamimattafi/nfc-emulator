package com.attafitamim.nfc.view.destinations.cards.scan.view

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import com.attafitamim.nfc.domain.model.cards.BankCard
import com.attafitamim.nfc.view.common.styles.DefaultPadding
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
    val state = viewModel.container.stateFlow.collectAsState().value
    BankCard(payload = state.cardPayload)
}

@Composable
fun BankCard(payload: BankCard.Payload?) {
    if (payload == null) {
        Text(
            text = "No scanned card",
            color = Color.White
        )

        return
    }

    Text(
        text = """
            Number: ${payload.cardNumber}
            Type: ${payload.cardType}
            Emv Raw: ${payload.emvRawData}
        """.trimIndent(),
        color = Color.White,
        modifier = DefaultPadding()
    )
}

@Composable
private fun LaunchSideEffects(
    viewModel: BankCardScanViewModel,
    navController: NavHostController
) = LaunchedEffect(viewModel) {

    viewModel.container.sideEffectFlow.collect { sideEffect ->
        when (sideEffect) {
            is BankCardScanSideEffect.OpenDestination -> {
                navController.toDestination(sideEffect.destination)
            }

            BankCardScanSideEffect.ShowCardSavedToast -> {}
            is BankCardScanSideEffect.ShowScanErrorToast -> {}
            BankCardScanSideEffect.ShowScanStartToast -> {}
        }
    }
}
