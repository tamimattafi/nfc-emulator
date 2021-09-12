package com.attafitamim.nfc.domain.usecase.crypto

import com.attafitamim.nfc.domain.crypto.ICryptoHandler

class DecryptData(private val cryptoHandler: ICryptoHandler) {
    suspend operator fun <T> invoke(encryptedData: String, clazz: Class<T>, password: String): T =
        cryptoHandler.decrypt(encryptedData, clazz, password)
}