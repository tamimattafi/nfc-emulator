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

    private fun commonDocumentDirPath(FolderName: String): File {
        val dir: File = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
                    .toString() + "/" + FolderName
            )
        } else {
            File(Environment.getExternalStorageDirectory().toString() + "/" + FolderName)
        }

        // Make sure the path directory exists.
        if (!dir.exists()) {
            dir.mkdirs()
        }

        return dir
    }

    private val logger by lazy {
        val logDirectory = commonDocumentDirPath("nfc_logs")

        val dateAndTime = SimpleDateFormat(
            "yyyy_MM_dd_HH_mm_ss_SSS",
            Locale.getDefault()
        ).format(Date())

        val logFile = File(logDirectory, "nfc_log_$dateAndTime.log")
        val logHandler = FileHandler(logFile.absolutePath, 1000 * 1024, 1000)
        logHandler.formatter = SimpleFormatter()
        Logger.getLogger(packageName).apply {
            addHandler(logHandler)
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        logger.log(
            Level.INFO,
            """
            $TAG
            onStartCommand() 
            intent: $intent"
            flags: $flags
            startId: $startId
            """.trimIndent()
        )

        if (intent.extras == null) {
            logger.log(
                Level.INFO,
                """
                $TAG
                onStartCommand() 
                Extras: null"
                """.trimIndent()
            )

            return START_NOT_STICKY
        }

        logger.log(
            Level.INFO,
            """
            $TAG
            onStartCommand() 
            Extras: ${intent.extras.asJson}"
            """.trimIndent()
        )

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
            logger.log(
                Level.INFO,
                """
                $TAG
                onStartCommand() 
                NDEF: ${ndefUri.asJson}"
                """.trimIndent()
            )

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
        logger.log(
            Level.INFO,
            """
            $TAG
            onStartForeground() 
            """.trimIndent()
        )
    }

    private fun createNotificationChannel() {
         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "nfc-emulator"
            val serviceChannel = NotificationChannel("nfc-emulator", channelName, NotificationManager.IMPORTANCE_LOW)
            getSystemService(NotificationManager::class.java).createNotificationChannel(serviceChannel)
         }

        logger.log(
            Level.INFO,
            """
            $TAG
            onCreateNotificationChannel() 
            """.trimIndent()
        )
    }

    override fun processCommandApdu(commandApdu: ByteArray, extras: Bundle?): ByteArray {
        logger.log(
            Level.INFO,
            """
            $TAG
            processCommandApdu() 
            incoming extras: ${extras?.asJson}
            incoming raw bytes: $commandApdu
            """.trimIndent()
        )

        //
        // The following flow is based on Appendix E "Example of Mapping Version 2.0 Command Flow"
        // in the NFC Forum specification
        //
        logger.log(
            Level.INFO,
            """
            $TAG
            processCommandApdu() 
            incoming commandApdu hex: ${NfcUtils.bytesToHex(commandApdu)}
            """.trimIndent()
        )

        logger.log(
            Level.INFO,
            """
            $TAG
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
            logger.log(
                Level.INFO,
                """
                $TAG
                processCommandApdu() 
                APDU_SELECT triggered
                Our response A_OKAY: ${ NfcUtils.bytesToHex(A_OKAY)}
                """.trimIndent()
            )

            return A_OKAY
        }

        logger.log(
            Level.INFO,
            """
            $TAG
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
            logger.log(
                Level.INFO,
                """
                $TAG
                processCommandApdu() 
                CAPABILITY_CONTAINER triggered
                Our response A_OKAY: ${ NfcUtils.bytesToHex(A_OKAY)}
                """.trimIndent()
            )

            return A_OKAY
        }

        logger.log(
            Level.INFO,
            """
            $TAG
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
            logger.log(
                Level.INFO,
                """
                $TAG
                processCommandApdu() 
                READ_CAPABILITY_CONTAINER triggered
                Our response READ_CAPABILITY_CONTAINER_RESPONSE: ${ NfcUtils.bytesToHex(READ_CAPABILITY_CONTAINER_RESPONSE)}
                """.trimIndent()
            )

            readCapabilityContainerCheck = true
            return READ_CAPABILITY_CONTAINER_RESPONSE
        }

        logger.log(
            Level.INFO,
            """
            $TAG
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
            logger.log(
                Level.INFO,
                """
                $TAG
                processCommandApdu() 
                NDEF_SELECT triggered
                Our response A_OKAY: ${ NfcUtils.bytesToHex(A_OKAY)}
                """.trimIndent()
            )

            return A_OKAY
        }

        logger.log(
            Level.INFO,
            """
            $TAG
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
            val start = byteArrayOf(
                0x00
            )

            // Build our response
            val response = ByteArray(start.size + ndefUriLength.size + A_OKAY.size)
            System.arraycopy(start, 0, response, 0, start.size)
            System.arraycopy(ndefUriLength, 0, response, start.size, ndefUriLength.size)
            System.arraycopy(A_OKAY, 0, response, start.size + ndefUriLength.size, A_OKAY.size)

            logger.log(
                Level.INFO,
                """
                $TAG
                processCommandApdu() 
                NDEF_READ_BINARY_NLEN triggered
                Our response: ${ NfcUtils.bytesToHex(response)}
                """.trimIndent()
            )

            return response
        }

        logger.log(
            Level.INFO,
            """
            $TAG
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
            val start = byteArrayOf(
                0x00
            )

            // Build our response
            val response =
                ByteArray(start.size + ndefUriLength.size + ndefUriBytes.size + A_OKAY.size)
            System.arraycopy(start, 0, response, 0, start.size)
            System.arraycopy(ndefUriLength, 0, response, start.size, ndefUriLength.size)
            System.arraycopy(
                ndefUriBytes,
                0,
                response,
                start.size + ndefUriLength.size,
                ndefUriBytes.size
            )
            System.arraycopy(
                A_OKAY,
                0,
                response,
                start.size + ndefUriLength.size + ndefUriBytes.size,
                A_OKAY.size
            )

            logger.log(
                Level.INFO,
                """
                $TAG
                processCommandApdu() 
                NDEF_READ_BINARY_GET_NDEF triggered
                Our response: ${ NfcUtils.bytesToHex(response)}
                """.trimIndent()
            )

            readCapabilityContainerCheck = false
            return response
        }

        logger.log(
            Level.INFO,
            """
            $TAG
            processCommandApdu() 
            UNKNOWN_COMMAND
            """.trimIndent()
        )

        logger.log(
            Level.INFO,
            """
            $TAG
            processCommandApdu() 
            Our response ndefUri.payload: ${ NfcUtils.bytesToHex(ndefUri.payload)}
            """.trimIndent()
        )

        return ndefUri.payload
    }

    override fun onDestroy() {
        logger.log(
            Level.INFO,
            """
            $TAG
            onDestroy() 
            """.trimIndent()
        )

        super.onDestroy()
    }

    override fun onCreate() {
        super.onCreate()
        logger.log(
            Level.INFO,
            """
            $TAG
            onCreate() 
            """.trimIndent()
        )

        Thread.setDefaultUncaughtExceptionHandler { thread, error ->
            logger.log(
                Level.INFO,
                """
                $TAG
                onUncaughtException() 
                Thread: $thread
                Exception: $error
                StackTrace: ${error.stackTrace.joinToString("\n")}
                """.trimIndent()
            )
        }
    }

    override fun onDeactivated(reason: Int) {
        logger.log(
            Level.INFO,
            """
            $TAG
            onDeactivated() 
            Reason: $reason
            """.trimIndent()
        )
    }

    companion object {
        const val NDEF_ENCODED_MESSAGE_KEY = "ndef_encoded_message"

        private const val TAG = "JDR HostApduService"

        //
        // We use the default AID from the HCE Android documentation
        // https://developer.android.com/guide/topics/connectivity/nfc/hce.html
        //
        // Ala... <aid-filter android:name="F0394148148100" />
        //
        private val APDU_SELECT = byteArrayOf(
            0x00,  // CLA	- Class - Class of instruction
            0xA4.toByte(),  // INS	- Instruction - Instruction code
            0x04,  // P1	- Parameter 1 - Instruction parameter 1
            0x00,  // P2	- Parameter 2 - Instruction parameter 2
            0x07,  // Lc field	- Number of bytes present in the data field of the command
            0xF0.toByte(),
            0x39,
            0x41,
            0x48,
            0x14,
            0x81.toByte(),
            0x00,  // NDEF Tag Application name
            0x00 // Le field	- Maximum number of bytes expected in the data field of the response to the command
        )

        private val CAPABILITY_CONTAINER = byteArrayOf(
            0x00,  // CLA	- Class - Class of instruction
            0xa4.toByte(),  // INS	- Instruction - Instruction code
            0x00,  // P1	- Parameter 1 - Instruction parameter 1
            0x0c,  // P2	- Parameter 2 - Instruction parameter 2
            0x02,  // Lc field	- Number of bytes present in the data field of the command
            0xe1.toByte(), 0x03 // file identifier of the CC file
        )

        private val READ_CAPABILITY_CONTAINER = byteArrayOf(
            0x00,  // CLA	- Class - Class of instruction
            0xb0.toByte(),  // INS	- Instruction - Instruction code
            0x00,  // P1	- Parameter 1 - Instruction parameter 1
            0x00,  // P2	- Parameter 2 - Instruction parameter 2
            0x0f // Lc field	- Number of bytes present in the data field of the command
        )

        private val READ_CAPABILITY_CONTAINER_RESPONSE = byteArrayOf(
            0x00, 0x0F,  // CCLEN length of the CC file
            0x20,  // Mapping Version 2.0
            0x00, 0x3B,  // MLe maximum 59 bytes R-APDU data size
            0x00, 0x34,  // MLc maximum 52 bytes C-APDU data size
            0x04,  // T field of the NDEF File Control TLV
            0x06,  // L field of the NDEF File Control TLV
            0xE1.toByte(), 0x04,  // File Identifier of NDEF file
            0x00, 0x32,  // Maximum NDEF file size of 50 bytes
            0x00,  // Read access without any security
            0x00,  // Write access without any security
            0x90.toByte(), 0x00 // A_OKAY
        )

        private val NDEF_SELECT = byteArrayOf(
            0x00,  // CLA	- Class - Class of instruction
            0xa4.toByte(),  // Instruction byte (INS) for Select command
            0x00,  // Parameter byte (P1), select by identifier
            0x0c,  // Parameter byte (P1), select by identifier
            0x02,  // Lc field	- Number of bytes present in the data field of the command
            0xE1.toByte(),
            0x04 // file identifier of the NDEF file retrieved from the CC file
        )

        private val NDEF_READ_BINARY_NLEN = byteArrayOf(
            0x00,  // Class byte (CLA)
            0xb0.toByte(),  // Instruction byte (INS) for ReadBinary command
            0x00, 0x00,  // Parameter byte (P1, P2), offset inside the CC file
            0x02 // Le field
        )

        private val NDEF_READ_BINARY_GET_NDEF = byteArrayOf(
            0x00,  // Class byte (CLA)
            0xb0.toByte(),  // Instruction byte (INS) for ReadBinary command
            0x00, 0x00,  // Parameter byte (P1, P2), offset inside the CC file
            0x0f //  Le field
        )

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