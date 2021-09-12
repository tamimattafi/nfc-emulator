package com.attafitamim.nfc.domain.usecase.cards.payload

import com.attafitamim.nfc.domain.model.cards.BankCard
import com.attafitamim.nfc.domain.usecase.crypto.EncryptData

class EncryptBankCardPayload(private val encryptData: EncryptData) {
    suspend operator fun invoke(payload: BankCard.Payload, password: String): String =
        encryptData.invoke(payload, password)
}