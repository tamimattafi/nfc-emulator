package com.attafitamim.nfc.emv

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.nfc.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.github.devnied.emvnfccard.parser.EmvTemplate

import com.github.devnied.emvnfccard.parser.IProvider
import android.nfc.tech.IsoDep
import android.util.Log

import android.widget.TextView
import android.widget.Toast
import java.lang.Exception


class MainActivity : AppCompatActivity() {

    private val nfcAdapter by lazy {
        val nfcManager = getSystemService(Context.NFC_SERVICE) as NfcManager
        nfcManager.defaultAdapter
    }

    private val parser by lazy {
        val config: EmvTemplate.Config = EmvTemplate.Config()
            .setContactLess(true)
            .setReadAllAids(true)
            .setReadTransactions(true)
            .setReadCplc(false)
            .setRemoveDefaultParsers(false)
            .setReadAt(true)

        EmvTemplate.Builder()
            .setProvider(provider)
            .setConfig(config)
            .build()
    }

    private val provider by lazy {
        NFCProvider()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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
        handleCard()
        enableNfcForegroundDispatch()
    }

    override fun onPause() {
        disableNfcForegroundDispatch()
        super.onPause()
    }

    private fun handleCard() {
        val tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG) as? Tag

        if (tag != null) {
            val isoDep = IsoDep.get(tag)
            if (isoDep != null) handleCard(isoDep)
        }
    }

    private fun handleCard(isoDep: IsoDep) {
        provider.tag = isoDep

        try {
            val card = parser.readEmvCard()
            findViewById<TextView>(R.id.txtCard).text = card.cardNumber
        } catch (e: Exception) {
            val message = e.message ?: e.toString()
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        }
    }
}