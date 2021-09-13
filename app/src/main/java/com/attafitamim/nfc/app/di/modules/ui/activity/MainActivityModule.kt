package com.attafitamim.nfc.app.di.modules.ui.activity

import android.content.Context
import android.nfc.NfcAdapter
import android.nfc.NfcManager
import com.attafitamim.nfc.view.activities.global.ActivityLifeCycleHandler
import com.attafitamim.nfc.view.activities.main.MainActivity
import com.attafitamim.nfc.view.nfc.INfcTagHost
import org.koin.dsl.module
import java.lang.IllegalStateException

val mainActivityModule = module {

    single<ActivityLifeCycleHandler<MainActivity>> {
        ActivityLifeCycleHandler()
    }

    factory<INfcTagHost> {
        val lifeCycleHandler = get<ActivityLifeCycleHandler<MainActivity>>()
        lifeCycleHandler.currentReference ?: throw IllegalStateException("activity is not running")
    }

    factory<NfcAdapter> {
        val context = get<Context>()
        val nfcManager = context.getSystemService(Context.NFC_SERVICE) as NfcManager
        nfcManager.defaultAdapter
    }
}