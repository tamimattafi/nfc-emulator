package com.attafitamim.nfc.view.destinations.cards.details.model

import com.attafitamim.nfc.domain.model.cards.BankCard

data class BankCardDetailsState(
    val bankCard: BankCard? = null,
    val cardPayload: BankCard.Payload? = null
)