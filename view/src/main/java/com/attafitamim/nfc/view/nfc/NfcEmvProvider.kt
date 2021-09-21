package com.attafitamim.nfc.view.nfc

import android.nfc.tech.IsoDep
import android.util.Log
import com.attafitamim.nfc.view.nfc.temp.NfcHostApduService.Companion.toHex
import com.github.devnied.emvnfccard.parser.IProvider

class NfcEmvProvider : IProvider {

    lateinit var tag: IsoDep

    override fun transceive(pCommand: ByteArray): ByteArray
        = tag.run {
            Log.d("APDU_TAG", "Transceive: ${pCommand.toHex()}")
            if (!isConnected) connect()
            val response = transceive(pCommand)
            Log.d("APDU_TAG", "Response: ${response.toHex()}")
            response
        }

    override fun getAt(): ByteArray {
        return tag.historicalBytes
    }
}