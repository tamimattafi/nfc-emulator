package com.attafitamim.nfc.view.destinations.cards.list.model

import com.attafitamim.nfc.view.navigation.NavigationDestination

sealed class BankCardsListSideEffect {
    class OpenDestination(
        val destination: NavigationDestination
    ) : BankCardsListSideEffect()
}