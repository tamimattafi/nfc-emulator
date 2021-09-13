package com.attafitamim.nfc.app.di.modules.data.source

import com.attafitamim.nfc.data.source.BankCardsLocalSource
import org.koin.dsl.module

val bankCardsSourceModule = module {

    factory {
        BankCardsLocalSource(database = get())
    }
}