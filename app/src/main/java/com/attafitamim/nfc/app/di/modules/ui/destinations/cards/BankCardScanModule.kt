package com.attafitamim.nfc.app.di.modules.ui.destinations.cards

import com.attafitamim.nfc.view.destinations.cards.scan.model.BankCardScanViewModel
import com.attafitamim.nfc.view.nfc.NfcEmvProvider
import com.github.devnied.emvnfccard.parser.EmvTemplate
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

val bankCardScanModule get() = module {

    factory { parametersHolder ->
        val config: EmvTemplate.Config = EmvTemplate.Config()
            .setContactLess(true)
            .setReadAllAids(true)
            .setReadTransactions(true)
            .setReadCplc(false)
            .setRemoveDefaultParsers(false)
            .setReadAt(true)

        val provider = parametersHolder.get<NfcEmvProvider>()
        EmvTemplate.Builder()
            .setProvider(provider)
            .setConfig(config)
            .build()
    }

    factory {
        NfcEmvProvider()
    }

    viewModel {
        val provider = get<NfcEmvProvider>()
        val parser = get<EmvTemplate> {
            parametersOf(provider)
        }

        BankCardScanViewModel(
            parser,
            provider,
            nfcHost = get(),
            addBankCard = get(),
            encryptBankCardPayload = get()
        )
    }
}