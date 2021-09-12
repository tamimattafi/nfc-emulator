package com.attafitamim.nfc.domain.usecase.cards.payload

import com.attafitamim.nfc.domain.model.cards.BankCard
import com.attafitamim.nfc.domain.usecase.crypto.DecryptData

class DecryptBankCardPayload(private val decryptData: DecryptData) {
    suspend operator fun invoke(encryptedPayload: String, password: String): BankCard.Payload =
        decryptData.invoke(encryptedPayload, BankCard.Payload::class.java, password)
}