package com.attafitamim.nfc.app.di.modules.data.usecase

import com.attafitamim.nfc.data.crypto.AesPbeHandler
import com.attafitamim.nfc.domain.usecase.cards.payload.DecryptBankCardPayload
import com.attafitamim.nfc.domain.usecase.cards.payload.EncryptBankCardPayload
import com.attafitamim.nfc.domain.usecase.cards.storage.AddBankCard
import com.attafitamim.nfc.domain.usecase.cards.storage.GetBankCard
import com.attafitamim.nfc.domain.usecase.cards.storage.GetBankCards
import com.attafitamim.nfc.domain.usecase.cards.storage.RemoveBankCard
import com.attafitamim.nfc.domain.usecase.crypto.DecryptData
import com.attafitamim.nfc.domain.usecase.crypto.EncryptData
import org.koin.dsl.module

val bankCardUseCaseModule get() = module {

    factory {
        GetBankCard(bankCardsRepository = get())
    }

    factory {
        GetBankCards(bankCardsRepository = get())
    }

    factory {
        AddBankCard(bankCardsRepository = get())
    }

    factory {
        RemoveBankCard(bankCardsRepository = get())
    }

    factory {
        val decryptData = DecryptData(cryptoHandler = get<AesPbeHandler>())
        DecryptBankCardPayload(decryptData)
    }

    factory {
        val encryptData = EncryptData(cryptoHandler = get<AesPbeHandler>())
        EncryptBankCardPayload(encryptData)
    }
}