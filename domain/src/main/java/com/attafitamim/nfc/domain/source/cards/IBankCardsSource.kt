package com.attafitamim.nfc.domain.source.cards

import com.attafitamim.nfc.domain.model.cards.BankCard

interface IBankCardsSource {
    suspend fun add(card: BankCard)
    suspend fun get(cardId: Int): BankCard
    suspend fun getPage(limit: Int, offset: Int): List<BankCard>
    suspend fun remove(cardId: Int)
}