package com.attafitamim.nfc.domain.usecase.crypto

import com.attafitamim.nfc.domain.crypto.ICryptoHandler

class EncryptData(private val cryptoHandler: ICryptoHandler) {
    suspend operator fun <T> invoke(data: T, password: String): String =
        cryptoHandler.encrypt(data, password)
}