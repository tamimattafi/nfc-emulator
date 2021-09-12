package com.attafitamim.nfc.domain.usecase.cards.storage

import com.attafitamim.nfc.domain.model.cards.BankCard
import com.attafitamim.nfc.domain.repository.cards.BankCardsRepository

class AddBankCard(private val bankCardsRepository: BankCardsRepository) {
    suspend operator fun invoke(bankCard: BankCard) = bankCardsRepository.add(bankCard)
}