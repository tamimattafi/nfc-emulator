package com.attafitamim.nfc.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.attafitamim.nfc.data.database.Database.Companion.EXPORT_SCHEME
import com.attafitamim.nfc.data.database.Database.Companion.VERSION
import com.attafitamim.nfc.data.database.dao.BankCardsDao
import com.attafitamim.nfc.data.model.BankCardEntity

@Database(
    entities = [BankCardEntity::class],
    version = VERSION,
    exportSchema = EXPORT_SCHEME
)
abstract class Database : RoomDatabase() {
    internal abstract val bankCardsDao: BankCardsDao

    companion object {
        const val NAME = "main-db"
        const val VERSION = 1
        const val EXPORT_SCHEME = false
    }
}