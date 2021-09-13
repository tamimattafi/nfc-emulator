package com.attafitamim.nfc.data.crypto

import android.util.Base64
import com.attafitamim.nfc.common.utils.asJsonBytes
import com.attafitamim.nfc.common.utils.readFromJson
import com.attafitamim.nfc.domain.crypto.ICryptoHandler
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

class AesPbeHandler : ICryptoHandler {

    private companion object {
        const val ALGORITHM = "AES/CBC/PKCS5PADDING"
        const val KEY_ALGORITHM = "PBKDF2WithHmacSHA512"

        const val PBE_KEY_ITERATION_COUNT = 10
        const val PBE_KEY_LENGTH = 128

        const val SALT_BYTES_SALT = 128
        const val IV_BYTES_SIZE = 128
    }

    override suspend fun <T> encrypt(
        data: T,
        key: String
    ): String {
        val dataBytes = data.asJsonBytes

        val secureRandom = SecureRandom()
        val ivBytes = ByteArray(IV_BYTES_SIZE)
        secureRandom.nextBytes(ivBytes)

        val salt = ByteArray(SALT_BYTES_SALT)
        secureRandom.nextBytes(salt)

        val cipher = createCipher(
            key,
            ivBytes,
            salt,
            Cipher.ENCRYPT_MODE
        )

        val encryptedData = cipher.doFinal(dataBytes)

        val encryptedDataSpec = EncryptedDataSpec(
            Base64.encodeToString(encryptedData, Base64.DEFAULT),
            Base64.encodeToString(ivBytes, Base64.DEFAULT),
            Base64.encodeToString(salt, Base64.DEFAULT)
        )

        return encryptedDataSpec.toString()
    }

    override suspend fun <T> decrypt(
        encryptedData: String,
        clazz: Class<T>,
        key: String
    ): T {
        val encryptedDataSpec = EncryptedDataSpec.fromString(encryptedData)
        val dataBytes = Base64.decode(encryptedDataSpec.data, Base64.DEFAULT)
        val ivBytes = Base64.decode(encryptedDataSpec.iv, Base64.DEFAULT)
        val salt = Base64.decode(encryptedDataSpec.salt, Base64.DEFAULT)

        val cipher = createCipher(
            key,
            ivBytes,
            salt,
            Cipher.DECRYPT_MODE
        )

        val decryptedData = cipher.doFinal(dataBytes)
        return decryptedData.readFromJson(clazz)
    }

    private fun createCipher(
        password: String,
        ivBytes: ByteArray,
        salt: ByteArray,
        mode: Int
    ): Cipher {
        val passwordChars = password.toCharArray()
        val pbeKeySpec = PBEKeySpec(
            passwordChars,
            salt,
            PBE_KEY_ITERATION_COUNT,
            PBE_KEY_LENGTH
        )

        val secret = SecretKeyFactory.getInstance(KEY_ALGORITHM)
            .generateSecret(pbeKeySpec)
            .encoded

        val ivSpec = IvParameterSpec(ivBytes)
        val secretKeySpec = SecretKeySpec(secret , ALGORITHM)

        return Cipher.getInstance(ALGORITHM).apply {
            init(mode, secretKeySpec, ivSpec)
        }
    }

    private class EncryptedDataSpec(
        val data: String,
        val iv: String,
        val salt: String
    ) {
        override fun toString(): String {
            val jsonData = this.asJsonBytes
            return Base64.encodeToString(jsonData, Base64.DEFAULT)
        }

        companion object {
            fun fromString(encryptedData: String): EncryptedDataSpec {
                val data = Base64.decode(encryptedData, Base64.DEFAULT)
                return data.readFromJson(EncryptedDataSpec::class.java)
            }
        }
    }
}