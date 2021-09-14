package com.attafitamim.nfc.view.destinations.cards.details.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import com.attafitamim.nfc.domain.model.cards.BankCard
import com.attafitamim.nfc.view.R
import com.attafitamim.nfc.view.common.styles.DefaultPadding
import com.attafitamim.nfc.view.common.widgets.TextLabel
import com.attafitamim.nfc.view.destinations.cards.details.model.BankCardDetailsSideEffect
import com.attafitamim.nfc.view.destinations.cards.details.model.BankCardDetailsViewModel
import com.attafitamim.nfc.view.destinations.cards.global.BankCardPayloadWidget
import com.attafitamim.nfc.view.destinations.cards.global.BankCardWidget
import com.attafitamim.nfc.view.destinations.cards.global.PasswordFormWidget
import kotlinx.coroutines.flow.collect

@Composable
fun BankCardDetailsWidget(
    viewModel: BankCardDetailsViewModel
) {
    SideEffectsContainer(viewModel = viewModel)
    val state = viewModel.container.stateFlow.collectAsState().value

    when {
        state.bankCard != null -> UnlockCardWidget(
            bankCard = state.bankCard,
            onSubmit = viewModel::unlockCard
        )

        state.cardPayload != null -> ScanCardWidget(
            bankCardPayload = state.cardPayload
        )
    }
}

@Composable
private fun ScanCardWidget(bankCardPayload: BankCard.Payload) {
    Column(
        modifier = DefaultPadding().fillMaxWidth()
    ) {
        BankCardPayloadWidget(payload = bankCardPayload)
        TextLabel(labelId = R.string.label_scan_terminal)
    }
}

@Composable
private fun UnlockCardWidget(
    bankCard: BankCard,
    onSubmit: (password: String) -> Unit
) {
    Column(
        modifier = DefaultPadding().fillMaxWidth()
    ) {
        BankCardWidget(bankCard = bankCard)
        PasswordFormWidget(onSubmit = onSubmit)
    }
}

@Composable
private fun SideEffectsContainer(
    viewModel: BankCardDetailsViewModel
) = LaunchedEffect(viewModel) {
    viewModel.container.sideEffectFlow.collect { sideEffect ->
        when (sideEffect) {
            BankCardDetailsSideEffect.ShowScanErrorToast -> TODO()
            BankCardDetailsSideEffect.ShowScanSuccessToast -> TODO()
        }
    }
}