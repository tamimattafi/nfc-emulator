package com.attafitamim.nfc.view.destinations.cards.scan.model

import com.attafitamim.nfc.domain.model.cards.BankCard

data class BankCardScanState(
    val cardPayload: BankCard.Payload? = null
)