package com.attafitamim.nfc.view.destinations.cards.scan.view

import androidx.compose.foundation.layout.Column
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import com.attafitamim.nfc.view.R
import com.attafitamim.nfc.view.common.styles.DefaultPadding
import com.attafitamim.nfc.view.common.widgets.TextLabel
import com.attafitamim.nfc.view.destinations.cards.global.BankCardPayloadWidget
import com.attafitamim.nfc.view.destinations.cards.global.PasswordFormWidget
import com.attafitamim.nfc.view.destinations.cards.scan.model.BankCardScanSideEffect
import com.attafitamim.nfc.view.destinations.cards.scan.model.BankCardScanViewModel
import com.attafitamim.nfc.view.navigation.NavigationDestination.Companion.toDestination
import kotlinx.coroutines.flow.collect

@Composable
fun BankCardScanWidget(
    viewModel: BankCardScanViewModel,
    navController: NavHostController
) {
    SideEffectsContainer(
        viewModel = viewModel,
        navController = navController
    )

    val state = viewModel.container.stateFlow.collectAsState().value
    if (state.cardPayload == null) {
        ScanPlaceHolder()
    } else {
        Column {
            BankCardPayloadWidget(payload = state.cardPayload)
            PasswordFormWidget(onSubmit = viewModel::saveCardPayload)
        }
    }
}

@Preview
@Composable
fun ScanPlaceHolder() {
    TextLabel(labelId = R.string.label_no_scanned_card)
}

@Composable
private fun SideEffectsContainer(
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
