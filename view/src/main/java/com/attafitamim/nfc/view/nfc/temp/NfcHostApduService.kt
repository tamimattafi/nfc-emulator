package com.attafitamim.nfc.view.nfc.temp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.nfc.cardemulation.HostApduService
import android.nfc.NdefRecord
import android.content.Intent
import android.graphics.BitmapFactory
import android.widget.Toast
import android.view.Gravity
import android.os.Bundle
import android.os.Environment
import android.util.Base64
import java.io.File
import java.math.BigInteger
import java.util.logging.FileHandler
import java.util.logging.Level
import java.util.logging.Logger
import java.util.logging.SimpleFormatter
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.compose.ui.text.toUpperCase
import androidx.core.app.NotificationCompat
import com.attafitamim.nfc.common.utils.asJson
import com.attafitamim.nfc.view.R
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by justin.ribeiro on 10/27/2014.
 *
 * The following definitions are based on two things:
 * 1. NFC Forum Type 4 Tag Operation Technical Specification, version 3.0 2014-07-30
 * 2. APDU example in libnfc: http://nfc-tools.org/index.php?title=Libnfc:APDU_example
 *
 */
class NfcHostApduService : HostApduService() {
    // In the scenario that we have done a CC read, the same byte[] match
    // for ReadBinary would trigger and we don't want that in succession
    private var readCapabilityContainerCheck = false
    private lateinit var ndefUri: NdefRecord
    private lateinit var ndefUriBytes: ByteArray
    private lateinit var ndefUriLength: ByteArray

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        if (intent.getBooleanExtra("closeService", false)) {
            stopSelf()
            return START_NOT_STICKY
        }

        if (intent.hasExtra(NDEF_ENCODED_MESSAGE_KEY)) {
            val encodedData = intent.getStringExtra(NDEF_ENCODED_MESSAGE_KEY)
            requireNotNull(encodedData)
            val encodedDataBytes = Base64.decode(encodedData, Base64.DEFAULT)

            ndefUri = NdefRecord(
                NdefRecord.TNF_WELL_KNOWN,
                NdefRecord.RTD_TEXT,
                NDEF_ID,
                encodedDataBytes
            )

            ndefUriBytes = ndefUri.toByteArray()
            ndefUriLength = BigInteger.valueOf(ndefUriBytes.size.toLong()).toByteArray()
            createNotificationChannel()
            showForegroundNotification()
        }

        return START_NOT_STICKY
    }


    private fun showForegroundNotification() {
        val contentTitle = "Nfc Emulator"
        val contentText = "Scan pos terminal to pay"

        val contentLogo = BitmapFactory.decodeResource(
            resources,
            R.drawable.ic_launcher_foreground
        )

        val intent = Intent(this, NfcHostApduService::class.java).apply {
            putExtra("closeService", true)
        }

        val pendingIntent = PendingIntent.getService(this, 0, intent, 0)

        val action = NotificationCompat.Action.Builder(
            android.R.drawable.ic_menu_close_clear_cancel,
            "Cancel",
            pendingIntent
        ).build()

        val notification = NotificationCompat.Builder(this, "nfc-emulator")
            .setContentTitle(contentTitle)
            .addAction(action)
            .setContentText(contentText)
            .setLargeIcon(contentLogo)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .build()

        startForeground(1, notification)
    }

    private fun createNotificationChannel() {
         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "nfc-emulator"
            val serviceChannel = NotificationChannel("nfc-emulator", channelName, NotificationManager.IMPORTANCE_LOW)
            getSystemService(NotificationManager::class.java).createNotificationChannel(serviceChannel)
         }
    }

    override fun processCommandApdu(commandApdu: ByteArray, extras: Bundle?): ByteArray {
        val commandHex = commandApdu.toHex().toUpperCase()
        Log.d("APDU_SERVICE", "--> $commandHex")
        val response = requestsDectionary[commandHex]?.toUpperCase()
        if (response != null) {
            Log.d("APDU_SERVICE", "<-- $response")
            return response.hexStringToByteArray()
        }

        Log.d("APDU_SERVICE", "<-- UNKNOWN_COMMAND")
        return ByteArray(0)
    }

    override fun onCreate() {
        super.onCreate()
        Thread.setDefaultUncaughtExceptionHandler { thread, error ->
            Log.d("APDU_SERVICE", "<-- ! Exception $error")
        }
    }

    override fun onDeactivated(reason: Int) {
        Log.d("APDU_SERVICE", "--> X Deactivate, reason $reason")
    }

    companion object {
        const val NDEF_ENCODED_MESSAGE_KEY = "ndef_encoded_message"

        fun ByteArray.toHex() = this.joinToString(separator = "") {
            it.toInt().and(0xff).toString(16).padStart(2, '0')
        }

        fun String.hexStringToByteArray() = ByteArray(this.length / 2) {
            this.substring(it * 2, it * 2 + 2).toInt(16).toByte()
        }

        val requestsDectionary = mapOf(
            "00A404000E325041592E5359532E444446303100" to "6f2f840e325041592e5359532e4444463031a51dbf0c1a61184f07a0000000041010500a4d6173746572636172648701019000",
            "00A4040007A000000004101000" to "6f338407a0000000041010a528500a4d6173746572636172645f2d047275656e870101bf0c0f9f4d020b0a9f6e07064300003030009000",
            "80A8000002830000" to "770a820219809404100104019000",
            "00B2011400" to "7081a057135586200087328099d24102011140390600000f5a0855862000873280995f24032410315f25032010015f280206435f3401018c219f02069f03069f1a0295055f2a029a039c019f37049f35019f45029f4c089f34038d0c910a8a0295059f37049f4c088e0e000000000000000042031e031f039f0702ffc09f080200029f0d05b4508400009f0e0500000000009f0f05b4708480009f420206439f4a01829000",
            "00B2010C00" to "7081a057135586200087328099d24102011140390600000f5a0855862000873280995f24032410315f25032010015f280206435f3401018c219f02069f03069f1a0295055f2a029a039c019f37049f35019f45029f4c089f34038d0c910a8a0295059f37049f4c088e0e000000000000000042031e031f039f0702ffc09f080200029f0d05b4508400009f0e0500000000009f0f05b4708480009f420206439f4a01829000",
            "00B2021400" to "7081a057135586200087328099d24102011140390600000f5a0855862000873280995f24032410315f25032010015f280206435f3401018c219f02069f03069f1a0295055f2a029a039c019f37049f35019f45029f4c089f34038d0c910a8a0295059f37049f4c088e0e000000000000000042031e031f039f0702ffc09f080200029f0d05b4508400009f0e0500000000009f0f05b4708480009f420206439f4a01829000",
            "00B2015C00" to "6A83",
        )

        private val NDEF_ID = byteArrayOf(
            0xE1.toByte(),
            0x04
        )
    }
}