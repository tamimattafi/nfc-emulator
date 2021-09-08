package com.attafitamim.nfc.emv

import com.github.devnied.emvnfccard.parser.IProvider
import android.nfc.tech.IsoDep
import com.github.devnied.emvnfccard.exception.CommunicationException
import java.io.IOException
import kotlin.Throws

class NFCProvider : IProvider {

    lateinit var tag: IsoDep

    override fun transceive(pCommand: ByteArray): ByteArray
        = try {
            if (!tag.isConnected) tag.connect()
            tag.transceive(pCommand)
        } catch (e: IOException) {
            ByteArray(0)
        }

    override fun getAt(): ByteArray {
        // For NFC-A
        return tag.historicalBytes
        // For NFC-B
        // return mTagCom.getHiLayerResponse();
    }
}