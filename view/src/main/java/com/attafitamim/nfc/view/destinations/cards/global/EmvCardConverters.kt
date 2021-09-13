package com.attafitamim.nfc.view.destinations.cards.global

import android.util.Base64
import com.attafitamim.nfc.domain.model.cards.BankCard
import com.github.devnied.emvnfccard.model.EmvCard
import fr.devnied.bitlib.BytesUtils

private const val HOLDER_NAME_SEPARATOR = " "

val EmvCard.asBankCardPayload: BankCard.Payload get() {
    val holderName = listOfNotNull(
        holderFirstname,
        holderLastname
    ).joinToString(HOLDER_NAME_SEPARATOR)

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