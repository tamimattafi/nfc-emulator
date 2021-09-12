package com.attafitamim.nfc.domain.model.cards

import java.util.*

data class BankCard(
    val id: Int,
    val displayNumber: String,
    val displayDate: String,
    val displayCardHolder: String?,
    val cardType: String,
    val cardLabel: String,
    val encryptedPayload: String,
    val creationDate: Date
) {
    data class Payload(
        val cardNumber: String,
        val cardDate: Date,
        val cardHolder: String?,
        val nfcRawData: String,
        val nfcBytesHex: String,
        val aidHex: String,
        val at: String
    )
}