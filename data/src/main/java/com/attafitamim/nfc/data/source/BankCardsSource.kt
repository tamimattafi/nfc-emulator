package com.attafitamim.nfc.data.source

import com.attafitamim.nfc.data.database.Database
import com.attafitamim.nfc.data.model.toCard
import com.attafitamim.nfc.data.model.toEntity
import com.attafitamim.nfc.domain.model.cards.BankCard
import com.attafitamim.nfc.domain.source.cards.IBankCardsSource

class BankCardsSource(
    private val database: Database
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