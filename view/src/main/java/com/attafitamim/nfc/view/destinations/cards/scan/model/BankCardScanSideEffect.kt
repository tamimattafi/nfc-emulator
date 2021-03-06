package com.attafitamim.nfc.view.destinations.cards.scan.model

import com.attafitamim.nfc.view.navigation.NavigationDestination

sealed class BankCardScanSideEffect {
    data class OpenDestination(val destination: NavigationDestination) : BankCardScanSideEffect()
    data class ShowScanErrorToast(val error: String) : BankCardScanSideEffect()
    object ShowCardSavedToast : BankCardScanSideEffect()
    object ShowScanStartToast : BankCardScanSideEffect()
}