package com.attafitamim.nfc.app.di.modules.ui.activity

import android.content.Context
import android.nfc.NfcManager
import org.koin.dsl.module

val mainActivityModule = module {

    factory {
        val context = get<Context>()
        val nfcManager = context.getSystemService(Context.NFC_SERVICE) as NfcManager
        nfcManager.defaultAdapter
    }
}