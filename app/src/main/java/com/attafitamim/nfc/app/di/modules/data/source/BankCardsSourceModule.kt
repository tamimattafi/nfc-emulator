package com.attafitamim.nfc.app.di.modules.data.source

import com.attafitamim.nfc.data.source.BankCardsLocalSource
import com.attafitamim.nfc.domain.source.cards.IBankCardsSource
import org.koin.dsl.module

val bankCardsSourceModule get() = module {

    factory<IBankCardsSource> {
        BankCardsLocalSource(database = get())
    }
}