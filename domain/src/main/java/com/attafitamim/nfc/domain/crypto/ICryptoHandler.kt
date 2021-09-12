package com.attafitamim.nfc.domain.crypto

interface ICryptoHandler {

    suspend fun <T> encrypt(
        data: T,
        password: String
    ): String

    suspend fun <T> decrypt(
        encryptedData: String,
        clazz: Class<T>,
        password: String
    ): T
}