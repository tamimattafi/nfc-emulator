package com.attafitamim.nfc.view.nfc

import android.nfc.tech.IsoDep
import com.github.devnied.emvnfccard.parser.IProvider

class NfcEmvProvider : IProvider {

    lateinit var tag: IsoDep

    override fun transceive(pCommand: ByteArray): ByteArray
        = tag.run {
            if (!isConnected) connect()
            transceive(pCommand)
        }

    override fun getAt(): ByteArray {
        return tag.historicalBytes
    }
}