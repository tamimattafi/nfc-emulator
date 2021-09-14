package com.attafitamim.nfc.view.destinations.cards.details.model

sealed class BankCardDetailsSideEffect {
    object ShowScanSuccessToast : BankCardDetailsSideEffect()
    object ShowScanErrorToast : BankCardDetailsSideEffect()
}