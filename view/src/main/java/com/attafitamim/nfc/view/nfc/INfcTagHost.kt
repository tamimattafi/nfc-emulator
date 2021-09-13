package com.attafitamim.nfc.view.nfc

interface INfcTagHost {
    fun registerListener(listener: INfcTagListener)
    fun unregisterListener(listener: INfcTagListener)
}