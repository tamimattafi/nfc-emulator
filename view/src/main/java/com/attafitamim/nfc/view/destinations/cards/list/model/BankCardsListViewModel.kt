package com.attafitamim.nfc.view.destinations.cards.list.model

import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.attafitamim.nfc.domain.model.cards.BankCard
import com.attafitamim.nfc.view.destinations.cards.details.view.BankCardDetailsDestination
import com.attafitamim.nfc.view.destinations.cards.list.source.BankCardsSource
import com.attafitamim.nfc.view.destinations.cards.scan.view.BankCardScanDestination
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container

class BankCardsListViewModel(
    private val bankCardsSource: BankCardsSource
) : ViewModel(), ContainerHost<BankCardsListState, BankCardsListSideEffect> {

    override val container by lazy {
        val initialState = BankCardsListState()
        container<BankCardsListState, BankCardsListSideEffect>(initialState)
    }

    init {
        loadData()
    }

    fun openBankCardDetails(bankCard: BankCard) = intent {
        val destination = BankCardDetailsDestination(bankCard.id)
        val sideEffect = BankCardsListSideEffect.OpenDestination(destination)
        postSideEffect(sideEffect)
    }

    fun openCardScan() = intent {
        val destination = BankCardScanDestination()
        val sideEffect = BankCardsListSideEffect.OpenDestination(destination)
        postSideEffect(sideEffect)
    }

    fun openCardCreation() = intent {

    }

    private fun loadData() = intent {
        val pagingConfig = PagingConfig(pageSize = DEFAULT_DISPLAY_ITEMS_SIZE)

        val cardsFlow = Pager(pagingConfig) { bankCardsSource }.flow
        reduce {
            state.copy(cardsFlow = cardsFlow)
        }
    }

    private companion object  {
        const val DEFAULT_DISPLAY_ITEMS_SIZE = 20
    }
}