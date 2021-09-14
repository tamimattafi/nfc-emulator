package com.attafitamim.nfc.app.di.modules.data.storage.database

import androidx.room.Room
import com.attafitamim.nfc.data.storage.database.MainDatabase
import org.koin.dsl.module

val databaseModule get() = module {

    single {
        Room.databaseBuilder(
            get(),
            MainDatabase::class.java,
            MainDatabase.NAME
        ).build()
    }
}