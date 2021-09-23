package com.attafitamim.nfc.view.nfc

import android.nfc.tech.IsoDep
import android.util.Log
import com.attafitamim.nfc.view.nfc.temp.NfcHostApduService.Companion.hexStringToByteArray
import com.attafitamim.nfc.view.nfc.temp.NfcHostApduService.Companion.toHex
import com.github.devnied.emvnfccard.parser.IProvider

class NfcEmvProvider : IProvider {

    lateinit var tag: IsoDep

    override fun transceive(pCommand: ByteArray): ByteArray
        = tag.run {
            testTransceive()
            Log.d("APDU_TAG", "Transceive: ${pCommand.toHex()}")
            if (!isConnected) connect()
            val response = transceive(pCommand)
            Log.d("APDU_TAG", "Response: ${response.toHex()}")
            response
        }

    private fun IsoDep.testTransceive() {
        if (!isConnected) connect()

        val firstCommand = "00A404000E325041592E5359532E444446303100"
        Log.d("APDU_TAG", "--> $firstCommand")
        val firstAnswer = transceive(firstCommand.hexStringToByteArray()).toHex()
        Log.d("APDU_TAG", "<-- $firstAnswer")
        val secondCommand = "00A4040007A000000004101000"
        Log.d("APDU_TAG", "--> $secondCommand")
        val secondAnswer = transceive(secondCommand.hexStringToByteArray()).toHex()
        Log.d("APDU_TAG", "<-- $secondAnswer")
        val thirdCommand = "80A8000002830000"
        Log.d("APDU_TAG", "--> $thirdCommand")
        val thirdAnswer = transceive(thirdCommand.hexStringToByteArray()).toHex()
        Log.d("APDU_TAG", "<-- $thirdAnswer")
        val forthCommand = "00B2011400"
        Log.d("APDU_TAG", "--> $forthCommand")
        val forthAnswer = transceive(forthCommand.hexStringToByteArray()).toHex()
        Log.d("APDU_TAG", "<-- $forthAnswer")
        val fifthCommand = "00B2021400"
        Log.d("APDU_TAG", "--> $fifthCommand")
        val fifthAnswer = transceive(forthCommand.hexStringToByteArray()).toHex()
        Log.d("APDU_TAG", "<-- $fifthAnswer")
        val sixthCommand = "00B2031400"
        Log.d("APDU_TAG", "--> $sixthCommand")
        val sixthAnswer = transceive(sixthCommand.hexStringToByteArray()).toHex()
        Log.d("APDU_TAG", "<-- $sixthAnswer")
    }

    override fun getAt(): ByteArray {
        return tag.historicalBytes
    }
}