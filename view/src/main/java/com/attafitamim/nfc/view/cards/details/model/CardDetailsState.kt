package com.attafitamim.nfc.view.cards.details.model

import com.attafitamim.nfc.domain.model.cards.BankCard

data class CardDetailsState(
    val card: BankCard? = null,
    val payload: BankCard.Payload? = null
)