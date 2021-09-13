package com.attafitamim.nfc.data.model

import com.attafitamim.nfc.domain.model.cards.BankCard
import java.util.*

internal fun BankCard.toEntity() = BankCardEntity(
    id,
    displayNumber,
    displayDate,
    displayCardHolder,
    cardType,
    encryptedPayload,
    creationDate.time
)

internal fun BankCardEntity.toCard() = BankCard(
    id,
    displayNumber,
    displayDate,
    displayCardHolder,
    cardType,
    encryptedPayload,
    Date(creationDate)
)