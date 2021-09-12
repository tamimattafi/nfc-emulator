package com.attafitamim.nfc.domain.usecase.cards.storage

import com.attafitamim.nfc.domain.model.cards.BankCard
import com.attafitamim.nfc.domain.repository.cards.BankCardsRepository

class GetBankCards(private val bankCardsRepository: BankCardsRepository) {
    suspend operator fun invoke(pageNumber: Int): List<BankCard> =
        bankCardsRepository.getPage(pageNumber)
}