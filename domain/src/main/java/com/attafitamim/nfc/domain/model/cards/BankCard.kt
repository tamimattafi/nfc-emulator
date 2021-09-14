package com.attafitamim.nfc.domain.model.cards

import java.util.*

data class BankCard(
    val id: Int,
    val displayNumber: String,
    val cardType: String,
    val encryptedPayload: String,
    val creationDate: Date
) {
    data class Payload(
        val cardNumber: String,
        val cardDate: Date,
        val cardHolder: String?,
        val cardType: String,
        val emvRawData: String,
        val encodedEmvBytes: String,
        val aidEncoded: String,
        val at: String
    )
}