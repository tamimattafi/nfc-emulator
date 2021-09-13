package com.attafitamim.nfc.domain.repository.cards

import com.attafitamim.nfc.domain.model.cards.BankCard
import com.attafitamim.nfc.domain.source.cards.IBankCardsSource

class BankCardsRepository(
    private val bankCardsLocalSource: IBankCardsSource
) {

    suspend fun add(card: BankCard) = bankCardsLocalSource.add(card)

    suspend fun get(cardId: Int) = bankCardsLocalSource.get(cardId)

    suspend fun getPage(pageNumber: Int): List<BankCard> {
        val limit = DEFAULT_LIMIT
        val offset = pageNumber * limit
        return bankCardsLocalSource.getPage(limit, offset)
    }

    suspend fun remove(cardId: Int) = bankCardsLocalSource.remove(cardId)

    private companion object {
        const val DEFAULT_LIMIT = 20
    }
}