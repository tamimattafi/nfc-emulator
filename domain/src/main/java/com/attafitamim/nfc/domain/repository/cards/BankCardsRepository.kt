package com.attafitamim.nfc.domain.repository.cards

import com.attafitamim.nfc.domain.model.cards.BankCard
import com.attafitamim.nfc.domain.source.cards.IBankCardsSource

class BankCardsRepository(
    private val bankCardsSource: IBankCardsSource
) {

    suspend fun add(card: BankCard) = bankCardsSource.add(card)

    suspend fun get(cardId: Int) = bankCardsSource.get(cardId)

    suspend fun getPage(pageNumber: Int): List<BankCard> {
        val limit = DEFAULT_LIMIT
        val offset = pageNumber * limit
        return bankCardsSource.getPage(limit, offset)
    }

    suspend fun remove(cardId: Int) = bankCardsSource.remove(cardId)

    private companion object {
        const val DEFAULT_LIMIT = 20
    }
}