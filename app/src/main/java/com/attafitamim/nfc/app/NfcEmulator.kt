package com.attafitamim.nfc.app

import android.app.Application
import com.attafitamim.nfc.app.di.KoinInitializer

class NfcEmulator : Application() {

    override fun onCreate() {
        super.onCreate()
        KoinInitializer.init(this)
    }
}