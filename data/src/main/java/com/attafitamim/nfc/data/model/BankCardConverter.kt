package com.attafitamim.nfc.data.model

import com.attafitamim.nfc.domain.model.cards.BankCard
import java.util.*

internal fun BankCard.toEntity() = BankCardEntity(
    id,
    displayNumber,
    cardType,
    encryptedPayload,
    creationDate.time
)

internal fun BankCardEntity.toCard() = BankCard(
    id,
    displayNumber,
    cardType,
    encryptedPayload,
    Date(creationDate)
)