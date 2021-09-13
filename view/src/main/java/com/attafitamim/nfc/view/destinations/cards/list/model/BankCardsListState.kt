package com.attafitamim.nfc.view.destinations.cards.list.model

import androidx.paging.PagingData
import com.attafitamim.nfc.domain.model.cards.BankCard
import kotlinx.coroutines.flow.Flow

data class BankCardsListState(
    val cardsFlow: Flow<PagingData<BankCard>>? = null
)