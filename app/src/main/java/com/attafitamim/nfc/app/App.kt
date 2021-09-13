package com.attafitamim.nfc.app

import android.app.Application
import com.attafitamim.nfc.app.di.KoinInitializer
import com.attafitamim.nfc.view.activities.global.ActivityLifeCycleHandler
import com.attafitamim.nfc.view.activities.main.MainActivity
import org.koin.android.ext.android.get

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        KoinInitializer.init(this)

        val activityLifeCycleHandler = get<ActivityLifeCycleHandler<MainActivity>>()
        registerActivityLifecycleCallbacks(activityLifeCycleHandler)
    }
}