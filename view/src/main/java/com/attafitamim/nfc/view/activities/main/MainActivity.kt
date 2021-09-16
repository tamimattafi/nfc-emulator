package com.attafitamim.nfc.view.activities.main

import android.app.PendingIntent
import android.content.ComponentName
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.cardemulation.CardEmulation
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.MaterialTheme
import androidx.navigation.compose.rememberNavController
import com.attafitamim.nfc.view.destinations.cards.details.view.BankCardDetailsDestination
import com.attafitamim.nfc.view.destinations.cards.list.view.BankCardsListDestination
import com.attafitamim.nfc.view.destinations.cards.scan.view.BankCardScanDestination
import com.attafitamim.nfc.view.navigation.NavigationHost
import com.attafitamim.nfc.view.nfc.INfcTagHost
import com.attafitamim.nfc.view.nfc.INfcTagListener
import com.attafitamim.nfc.view.nfc.temp.NfcHostApduService
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity(), INfcTagHost {

    private val nfcAdapter: NfcAdapter by inject()
    private val cardEmulation: CardEmulation by inject()

    private val nfcTagListeners by lazy {
        HashSet<INfcTagListener>()
    }

    private val nfcServiceName by lazy {
        ComponentName(this, NfcHostApduService::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                val navController = rememberNavController()
                NavigationHost(
                    navController = navController,
                    initialDestination = BankCardsListDestination::class.java,
                    BankCardScanDestination::class.java,
                    BankCardsListDestination::class.java,
                    BankCardDetailsDestination::class.java
                )
            }
        }
    }

    private fun enableNfcForegroundDispatch() {
        val intent = Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        val nfcPendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
        nfcAdapter.enableForegroundDispatch(this, nfcPendingIntent, null, null)
    }

    private fun disableNfcForegroundDispatch() {
        nfcAdapter.disableForegroundDispatch(this)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleCard()
    }

    override fun onResume() {
        super.onResume()
        cardEmulation.setPreferredService(this, componentName)
        enableNfcForegroundDispatch()
        handleCard()
    }

    override fun onPause() {
        cardEmulation.unsetPreferredService(this)
        disableNfcForegroundDispatch()
        super.onPause()
    }

    private fun getCard(): Tag?
        = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG) as? Tag

    private fun handleCard() {
        val tag = this.getCard() ?: return

        nfcTagListeners.forEach { listener ->
            listener.onNewTag(tag = tag)
        }
    }

    override fun registerListener(listener: INfcTagListener) {
        nfcTagListeners.add(listener)

        val tag = this.getCard() ?: return
        listener.onNewTag(tag)
    }

    override fun unregisterListener(listener: INfcTagListener) {
        nfcTagListeners.remove(listener)
    }
}