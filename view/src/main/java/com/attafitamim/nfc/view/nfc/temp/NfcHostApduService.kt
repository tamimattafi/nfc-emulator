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
        Log.d("APDU_SERVICE",
            """
            
            processCommandApdu() 
            incoming extras: ${extras?.asJson}
            incoming raw bytes: $commandApdu
            """.trimIndent()
        )

        //
        // The following flow is based on Appendix E "Example of Mapping Version 2.0 Command Flow"
        // in the NFC Forum specification
        //
        Log.d("APDU_SERVICE",
            """
            
            processCommandApdu() 
            incoming commandApdu hex: ${NfcUtils.bytesToHex(commandApdu)}
            """.trimIndent()
        )

        Log.d("APDU_SERVICE",
            """
            
            check APDU_SELECT
            """.trimIndent()
        )

        //
        // First command: NDEF Tag Application select (Section 5.5.2 in NFC Forum spec)
        //
        if (NfcUtils.isEqual(
                APDU_SELECT,
                commandApdu
            )
        ) {
            Log.d("APDU_SERVICE",
                """
                
                processCommandApdu() 
                APDU_SELECT triggered
                Our response APDU_SELECT_RESPONSE: ${ NfcUtils.bytesToHex(APDU_SELECT_RESPONSE)}
                """.trimIndent()
            )

            return APDU_SELECT_RESPONSE
        }

        Log.d("APDU_SERVICE",
            """
            
            check CAPABILITY_CONTAINER
            """.trimIndent()
        )

        //
        // Second command: Capability Container select (Section 5.5.3 in NFC Forum spec)
        //
        if (NfcUtils.isEqual(
                CAPABILITY_CONTAINER,
                commandApdu
            )
        ) {
            Log.d("APDU_SERVICE",
                """
                
                processCommandApdu() 
                CAPABILITY_CONTAINER triggered
                Our response CAPABILITY_CONTAINER_RESPONSE: ${ NfcUtils.bytesToHex(CAPABILITY_CONTAINER_RESPONSE)}
                """.trimIndent()
            )

            return CAPABILITY_CONTAINER_RESPONSE
        }

        Log.d("APDU_SERVICE",
            """
            
            check READ_CAPABILITY_CONTAINER
            """.trimIndent()
        )

        //
        // Third command: ReadBinary data from CC file (Section 5.5.4 in NFC Forum spec)
        //
        if (NfcUtils.isEqual(
                READ_CAPABILITY_CONTAINER,
                commandApdu
            ) && !readCapabilityContainerCheck
        ) {
            Log.d("APDU_SERVICE",
                """
                
                processCommandApdu() 
                READ_CAPABILITY_CONTAINER triggered
                Our response READ_CAPABILITY_CONTAINER_RESPONSE: ${ NfcUtils.bytesToHex(READ_CAPABILITY_CONTAINER_RESPONSE)}
                """.trimIndent()
            )

            readCapabilityContainerCheck = true
            return READ_CAPABILITY_CONTAINER_RESPONSE
        }

        Log.d("APDU_SERVICE",
            """
            
            check NDEF_SELECT
            """.trimIndent()
        )

        //
        // Fourth command: NDEF Select command (Section 5.5.5 in NFC Forum spec)
        //
        if (NfcUtils.isEqual(
                NDEF_SELECT,
                commandApdu
            )
        ) {
            Log.d("APDU_SERVICE",
                """
                
                processCommandApdu() 
                NDEF_SELECT triggered
                Our response NDEF_SELECT_RESPONSE: ${ NfcUtils.bytesToHex(NDEF_SELECT_RESPONSE)}
                """.trimIndent()
            )

            return NDEF_SELECT_RESPONSE
        }

        Log.d("APDU_SERVICE",
            """
            
            check NDEF_READ_BINARY_NLEN
            """.trimIndent()
        )

        //
        // Fifth command:  ReadBinary, read NLEN field
        //
        if (NfcUtils.isEqual(
                NDEF_READ_BINARY_NLEN,
                commandApdu
            )
        ) {

            Log.d("APDU_SERVICE",
                """
                
                processCommandApdu() 
                NDEF_READ_BINARY_NLEN triggered
                Our response NDEF_READ_BINARY_NLEN_RESPONSE: ${ NfcUtils.bytesToHex(NDEF_READ_BINARY_NLEN_RESPONSE)}
                """.trimIndent()
            )

            return NDEF_READ_BINARY_NLEN_RESPONSE
        }

        Log.d("APDU_SERVICE",
            """
            
            check NDEF_READ_BINARY_GET_NDEF
            """.trimIndent()
        )

        //
        // Sixth command: ReadBinary, get NDEF data
        //
        if (NfcUtils.isEqual(
                NDEF_READ_BINARY_GET_NDEF,
                commandApdu
            )
        ) {

            Log.d("APDU_SERVICE",
                """
                
                processCommandApdu() 
                NDEF_READ_BINARY_GET_NDEF triggered
                our response NDEF_READ_BINARY_GET_NDEF_RESPONSE: ${NDEF_READ_BINARY_GET_NDEF_RESPONSE.toHex()}
                """.trimIndent()
            )

            readCapabilityContainerCheck = false
            return NDEF_READ_BINARY_GET_NDEF_RESPONSE
        }


        if (
            NfcUtils.isEqual(
                PRE_LAST_STEP,
                commandApdu
            )
        ) {
            Log.d("APDU_SERVICE",
                """
                
                processCommandApdu() 
                PRE_LAST_STEP triggered
                Our response PRE_LAST_STEP_RESPONSE: ${ NfcUtils.bytesToHex(PRE_LAST_STEP_RESPONSE)}
                """.trimIndent()
            )

            return PRE_LAST_STEP_RESPONSE
        }

        Log.d("APDU_SERVICE",
            """
            
            processCommandApdu() 
            UNKNOWN_COMMAND
            """.trimIndent()
        )

        Log.d("APDU_SERVICE",
            """
            
            processCommandApdu() 
            Our response ndefUri.payload: ${ NfcUtils.bytesToHex(ndefUri.payload)}
            """.trimIndent()
        )

        return ndefUri.payload
    }

    override fun onDestroy() {
        Log.d("APDU_SERVICE",
            """
            
            onDestroy() 
            """.trimIndent()
        )

        super.onDestroy()
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("APDU_SERVICE",
            """
            
            onCreate() 
            """.trimIndent()
        )

        Thread.setDefaultUncaughtExceptionHandler { thread, error ->
            Log.d("APDU_SERVICE",
                """
                
                onUncaughtException() 
                Thread: $thread
                Exception: $error
                StackTrace: ${error.stackTrace.joinToString("\n")}
                """.trimIndent()
            )
        }
    }

    override fun onDeactivated(reason: Int) {
        Log.d("APDU_SERVICE",
            """
            
            onDeactivated() 
            Reason: $reason
            """.trimIndent()
        )
    }

    companion object {
        const val NDEF_ENCODED_MESSAGE_KEY = "ndef_encoded_message"

        private const val TAG = "JDR HostApduService"

        fun ByteArray.toHex() = this.joinToString(separator = "") {
            it.toInt().and(0xff).toString(16).padStart(2, '0')
        }

        fun String.hexStringToByteArray() = ByteArray(this.length / 2) {
            this.substring(it * 2, it * 2 + 2).toInt(16).toByte()
        }

        //
        // We use the default AID from the HCE Android documentation
        // https://developer.android.com/guide/topics/connectivity/nfc/hce.html
        // Ala... <aid-filter android:name="F0394148148100" />
        //
        private val APDU_SELECT get() =
            "00A404000E325041592E5359532E444446303100".hexStringToByteArray()

        private val APDU_SELECT_RESPONSE get() =
            "6f23840e325041592e5359532e4444463031a511bf0c0e610c4f07a00000000430608701019000".hexStringToByteArray()

        private val CAPABILITY_CONTAINER get() =
            "00A4040007A000000004306000".hexStringToByteArray()

        private val CAPABILITY_CONTAINER_RESPONSE get() =
            "6f328407a0000000043060a52750074d41455354524f8701015f2d0264659f1101019f12074d61657374726fbf0c059f4d020b0a9000".hexStringToByteArray()

        private val READ_CAPABILITY_CONTAINER get() =
            "80A8000002830000".hexStringToByteArray()

        private val READ_CAPABILITY_CONTAINER_RESPONSE get() =
            "7716820219809410080101001001010118010200200101009000".hexStringToByteArray()

        private val NDEF_SELECT get() =
            "00B2010C00".hexStringToByteArray()

        private val NDEF_SELECT_RESPONSE get() =
            "7081a057135586200087328099d24102011140390600000f5a0855862000873280995f24032410315f25032010015f280206435f3401018c219f02069f03069f1a0295055f2a029a039c019f37049f35019f45029f4c089f34038d0c910a8a0295059f37049f4c088e0e000000000000000042031e031f039f0702ffc09f080200029f0d05b4508400009f0e0500000000009f0f05b4708480009f420206439f4a01829000".hexStringToByteArray()

        private val NDEF_READ_BINARY_NLEN get() =
            "00B2011400".hexStringToByteArray()

        private val NDEF_READ_BINARY_NLEN_RESPONSE get() =
            "9f4f1a9f27019f02065f2a029a039f36029f5206df3e019f21039f7c149000".hexStringToByteArray()

        private val NDEF_READ_BINARY_GET_NDEF get() =
            "00B2011C00".hexStringToByteArray()

        private val NDEF_READ_BINARY_GET_NDEF_RESPONSE get() =
            "703b5a0854133390000015138c219f02069f03069f1a0295055f2a029a039c019f37049f35019f45029f4c089f34038d0c910a8a0295059f37049f4c089000".hexStringToByteArray()

        private val PRE_LAST_STEP get() =
            "00B2015C00".hexStringToByteArray()

        private val PRE_LAST_STEP_RESPONSE get() =
            "6A83".hexStringToByteArray()

        private val A_OKAY = byteArrayOf(
            0x90.toByte(),  // SW1	Status byte 1 - Command processing status
            0x00 // SW2	Status byte 2 - Command processing qualifier
        )

        private val NDEF_ID = byteArrayOf(
            0xE1.toByte(),
            0x04
        )
    }
}