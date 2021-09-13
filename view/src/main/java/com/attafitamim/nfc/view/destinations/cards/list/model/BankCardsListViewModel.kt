package com.attafitamim.nfc.view.destinations.cards.list.model

import androidx.lifecycle.ViewModel
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container

class BankCardsListViewModel : ViewModel(),
    ContainerHost<BankCardsListState, BankCardsListSideEffect> {

    override val container by lazy {
        val initialState = BankCardsListState()
        container<BankCardsListState, BankCardsListSideEffect>(initialState)
    }
}