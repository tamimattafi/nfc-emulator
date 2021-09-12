package com.attafitamim.nfc.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.attafitamim.nfc.data.database.LocalTables

@Entity(tableName = LocalTables.BANK_CARDS_TABLE)
internal data class BankCardEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val displayNumber: String,
    val displayDate: String,
    val displayCardHolder: String?,
    val cardType: String,
    val cardLabel: String,
    val encryptedPayload: String,
    val creationDate: Long
)