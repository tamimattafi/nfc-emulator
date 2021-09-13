package com.attafitamim.nfc.domain.crypto

interface ICryptoHandler {

    suspend fun <T> encrypt(
        data: T,
        key: String
    ): String

    suspend fun <T> decrypt(
        encryptedData: String,
        clazz: Class<T>,
        key: String
    ): T
}