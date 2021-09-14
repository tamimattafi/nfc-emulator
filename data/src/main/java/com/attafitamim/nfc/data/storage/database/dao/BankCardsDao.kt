package com.attafitamim.nfc.data.storage.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.attafitamim.nfc.data.storage.database.LocalTables.BANK_CARDS_TABLE
import com.attafitamim.nfc.data.model.BankCardEntity

@Dao
internal interface BankCardsDao {

    @Insert
    suspend fun addCard(card: BankCardEntity)

    @Query("SELECT * FROM $BANK_CARDS_TABLE WHERE id = :cardId")
    suspend fun getCard(cardId: Int): BankCardEntity

    @Query("SELECT * FROM $BANK_CARDS_TABLE LIMIT :limit OFFSET :offset")
    suspend fun getCards(limit: Int, offset: Int): List<BankCardEntity>

    @Query("DELETE FROM $BANK_CARDS_TABLE WHERE id = :cardId")
    suspend fun deleteCard(cardId: Int)
}