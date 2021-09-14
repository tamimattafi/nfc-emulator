package com.attafitamim.nfc.view.destinations.cards.details.model

import androidx.lifecycle.ViewModel
import com.attafitamim.nfc.domain.usecase.cards.payload.DecryptBankCardPayload
import com.attafitamim.nfc.domain.usecase.cards.storage.GetBankCard
import com.attafitamim.nfc.view.destinations.cards.details.view.BankCardDetailsDestination
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container

class BankCardDetailsViewModel(
    private val destination: BankCardDetailsDestination,
    private val getBankCard: GetBankCard,
    private val decryptBankCardPayload: DecryptBankCardPayload
) : ViewModel(), ContainerHost<BankCardDetailsState, BankCardDetailsSideEffect> {

    override val container by lazy {
        val initialState = BankCardDetailsState()
        container<BankCardDetailsState, BankCardDetailsSideEffect>(initialState)
    }

    init {
        loadData()
    }

    fun unlockCard(password: String) = intent {
        val bankCard = state.bankCard ?: return@intent
        val cardPayload = decryptBankCardPayload(bankCard.encryptedPayload, password)
        reduce {
            state.copy(bankCard = null, cardPayload = cardPayload)
        }

        postSideEffect(BankCardDetailsSideEffect.ShowUnlockSuccessMessage)

        val sideEffect = BankCardDetailsSideEffect.StartNfcApduService(cardPayload.encodedEmvBytes)
        postSideEffect(sideEffect)
    }

    override fun onCleared() = intent {
        postSideEffect(BankCardDetailsSideEffect.StopNfcEmulationService)
        super.onCleared()
    }

    private fun loadData() = intent {
        val bankCard = getBankCard(destination.cardId)
        reduce {
            state.copy(bankCard = bankCard)
        }
    }
}