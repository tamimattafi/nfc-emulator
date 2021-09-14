package com.attafitamim.nfc.app.di.modules.data.crypto

import com.attafitamim.nfc.data.crypto.AesPbeHandler
import org.koin.dsl.module

val cryptoModule get() = module {

    factory {
        AesPbeHandler()
    }
}