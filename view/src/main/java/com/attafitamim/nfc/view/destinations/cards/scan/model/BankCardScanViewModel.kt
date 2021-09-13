package com.attafitamim.nfc.view.destinations.cards.scan.model

import android.nfc.tech.IsoDep
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.attafitamim.nfc.view.destinations.cards.global.asBankCardPayload
import com.attafitamim.nfc.view.destinations.cards.list.model.BankCardsListSideEffect
import com.attafitamim.nfc.view.destinations.cards.scan.source.NfcEmvProvider
import com.github.devnied.emvnfccard.parser.EmvTemplate
import com.github.devnied.emvnfccard.parser.IProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import java.lang.Exception

class BankCardScanViewModel(
    private val parser: EmvTemplate,
    private val provider: NfcEmvProvider
) : ViewModel(), ContainerHost<BankCardScanState, BankCardScanSideEffect> {

    override val container by lazy {
        val initialState = BankCardScanState()
        container<BankCardScanState, BankCardScanSideEffect>(initialState)
    }

    fun readCard(isoDep: IsoDep) = intent {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                provider.tag = isoDep
                val card = parser.readEmvCard()
                val cardPayload = card.asBankCardPayload

                reduce {
                    state.copy(cardPayload = cardPayload)
                }
            } catch (e: Exception) {
                val errorMessage = e.message ?: e.toString()
                val sideEffect = BankCardScanSideEffect.ShowScanErrorToast(errorMessage)
                postSideEffect(sideEffect)
            }
        }
    }
}