package com.attafitamim.nfc.app.di.modules.ui.destinations.cards

import com.attafitamim.nfc.view.destinations.cards.details.model.BankCardDetailsViewModel
import org.koin.dsl.module

val bankCardDetailsModule get() = module {

    factory { parameters ->
        BankCardDetailsViewModel(
            destination = parameters.get(),
            getBankCard = get(),
            decryptBankCardPayload = get()
        )
    }
}