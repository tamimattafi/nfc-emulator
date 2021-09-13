package com.attafitamim.nfc.app.di

import android.content.Context
import com.attafitamim.nfc.app.di.modules.data.crypto.cryptoModule
import com.attafitamim.nfc.app.di.modules.data.repository.repositoryModule
import com.attafitamim.nfc.app.di.modules.data.source.bankCardsSourceModule
import com.attafitamim.nfc.app.di.modules.data.storage.database.databaseModule
import com.attafitamim.nfc.app.di.modules.data.usecase.bankCardUseCaseModule
import com.attafitamim.nfc.app.di.modules.ui.activity.mainActivityModule
import com.attafitamim.nfc.app.di.modules.ui.destinations.cards.bankCardScanModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

object KoinInitializer {

    fun init(context: Context) {
        startKoin {
            androidContext(context)

            modules(
                mainActivityModule,
                cryptoModule,
                repositoryModule,
                bankCardsSourceModule,
                databaseModule,
                bankCardUseCaseModule,
                bankCardScanModule
            )
        }
    }
}