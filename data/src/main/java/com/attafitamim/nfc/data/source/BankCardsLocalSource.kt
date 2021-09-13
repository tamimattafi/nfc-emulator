package com.attafitamim.nfc.data.source

import com.attafitamim.nfc.data.storage.database.MainDatabase
import com.attafitamim.nfc.data.model.toCard
import com.attafitamim.nfc.data.model.toEntity
import com.attafitamim.nfc.domain.model.cards.BankCard
import com.attafitamim.nfc.domain.source.cards.IBankCardsSource

class BankCardsLocalSource(
    private val database: MainDatabase
) : IBankCardsSource {

    override suspend fun add(card: BankCard) {
        val entity = card.toEntity()
        database.bankCardsDao.addCard(entity)
    }

    override suspend fun get(cardId: Int): BankCard =
        database.bankCardsDao.getCard(cardId).toCard()

    override suspend fun getPage(limit: Int, offset: Int): List<BankCard> =
        database.bankCardsDao.getCards(limit, offset).map { card ->
            card.toCard()
        }

    override suspend fun remove(cardId: Int) {
        database.bankCardsDao.deleteCard(cardId)
    }
}