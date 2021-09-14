package com.attafitamim.nfc.view.destinations.cards.global

import android.util.Base64
import com.attafitamim.nfc.domain.model.cards.BankCard
import com.github.devnied.emvnfccard.model.EmvCard
import fr.devnied.bitlib.BytesUtils
import java.lang.StringBuilder
import java.util.*

private const val DEFAULT_SEPARATOR = " "
private const val DISPLAY_NUMBERS_SIZE = 4

fun BankCard.Payload.toBankCard(encryptedPayload: String): BankCard {
    val keptNumbersStartIndex = cardNumber.length - DISPLAY_NUMBERS_SIZE
    val keptNumbers = cardNumber.substring(keptNumbersStartIndex)

    return BankCard(
        id = 0,
        displayNumber = keptNumbers,
        cardType = cardType,
        encryptedPayload = encryptedPayload,
        creationDate = Date()
    )
}

val EmvCard.asBankCardPayload: BankCard.Payload get() {
    val holderName = listOfNotNull(
        holderFirstname,
        holderLastname
    ).joinToString(DEFAULT_SEPARATOR)

    val application = applications.first()
    val raw = track2?.raw ?: track1?.raw
    val nfcRawData = BytesUtils.bytesToStringNoSpace(raw)
    val encodedNfcBytes = Base64.encodeToString(raw, Base64.DEFAULT)
    val encodedAid = Base64.encodeToString(application.aid, Base64.DEFAULT)

    return BankCard.Payload(
        cardNumber,
        expireDate,
        holderName,
        type.getName(),
        nfcRawData,
        encodedNfcBytes,
        encodedAid,
        at
    )
}