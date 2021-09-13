package com.attafitamim.nfc.view.nfc

import android.nfc.Tag

interface INfcTagListener {
    fun onNewTag(tag: Tag)
}