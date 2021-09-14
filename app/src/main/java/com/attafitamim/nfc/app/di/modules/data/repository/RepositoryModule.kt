package com.attafitamim.nfc.app.di.modules.data.repository

import com.attafitamim.nfc.domain.repository.cards.BankCardsRepository
import org.koin.dsl.module

val repositoryModule get() = module {

    factory {
        BankCardsRepository(bankCardsLocalSource = get())
    }
}