package com.attafitamim.nfc.domain.usecase.cards.storage

import com.attafitamim.nfc.domain.repository.cards.BankCardsRepository

class RemoveBankCard(private val bankCardsRepository: BankCardsRepository) {
    suspend operator fun invoke(cardId: Int) = bankCardsRepository.remove(cardId)
}