package com.attafitamim.nfc.app.di.modules.ui.destinations.cards

import com.attafitamim.nfc.view.destinations.cards.list.model.BankCardsListViewModel
import com.attafitamim.nfc.view.destinations.cards.list.source.BankCardsSource
import org.koin.dsl.module

val bankCardsListModule get() = module {

    factory {
        BankCardsSource(getTasksPage = get())
    }

    factory {
        BankCardsListViewModel(bankCardsSource = get())
    }
}