package com.attafitamim.nfc.view.destinations.cards.details.model

sealed class BankCardDetailsSideEffect {
    object ShowUnlockSuccessMessage : BankCardDetailsSideEffect()
    data class StartNfcApduService(val emvBytes: String) : BankCardDetailsSideEffect()
}