package com.attafitamim.nfc.view.destinations.cards.details.view

import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewModelScope
import com.attafitamim.nfc.domain.model.cards.BankCard
import com.attafitamim.nfc.view.R
import com.attafitamim.nfc.view.common.styles.DefaultPadding
import com.attafitamim.nfc.view.common.widgets.TextLabel
import com.attafitamim.nfc.view.destinations.cards.details.model.BankCardDetailsSideEffect
import com.attafitamim.nfc.view.destinations.cards.details.model.BankCardDetailsViewModel
import com.attafitamim.nfc.view.destinations.cards.global.BankCardPayloadWidget
import com.attafitamim.nfc.view.destinations.cards.global.BankCardWidget
import com.attafitamim.nfc.view.destinations.cards.global.PasswordFormWidget
import com.attafitamim.nfc.view.nfc.temp.NfcHostApduService
import kotlinx.coroutines.flow.collect

@Composable
fun BankCardDetailsWidget(
    viewModel: BankCardDetailsViewModel
) {
    SideEffectsContainer(viewModel = viewModel)
    val state = viewModel.container.stateFlow.collectAsState().value

    Column(
        modifier = DefaultPadding().fillMaxWidth()
    ) {
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
}

@Composable
private fun ScanCardWidget(bankCardPayload: BankCard.Payload) {
    BankCardPayloadWidget(payload = bankCardPayload)
    TextLabel(labelId = R.string.label_scan_terminal)
}

@Composable
private fun UnlockCardWidget(
    bankCard: BankCard,
    onSubmit: (password: String) -> Unit
) {
    BankCardWidget(bankCard = bankCard)
    PasswordFormWidget(onSubmit = onSubmit)
}

@Composable
private fun SideEffectsContainer(viewModel: BankCardDetailsViewModel) {
    val context = LocalContext.current
    LaunchedEffect(viewModel.viewModelScope) {
        viewModel.container.sideEffectFlow.collect { sideEffect ->
            when (sideEffect) {
                BankCardDetailsSideEffect.ShowUnlockSuccessMessage -> {
                    Toast.makeText(
                        context,
                        R.string.label_card_unlocked,
                        Toast.LENGTH_LONG
                    ).show()
                }

                is BankCardDetailsSideEffect.StartNfcApduService -> {
                    val intent = Intent(context, NfcHostApduService::class.java).apply {
                        putExtra(NfcHostApduService.NDEF_ENCODED_MESSAGE_KEY, sideEffect.emvBytes)
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        context.startForegroundService(intent)
                    } else {
                        context.startService(intent)
                    }
                }
            }
        }
    }
}