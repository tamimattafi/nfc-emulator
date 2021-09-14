package com.attafitamim.nfc.view.destinations.cards.global

import androidx.compose.foundation.layout.Column
import androidx.compose.material.OutlinedButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.attafitamim.nfc.domain.model.cards.BankCard
import com.attafitamim.nfc.view.R
import com.attafitamim.nfc.view.common.styles.DefaultPadding

@Composable
fun BankCardPayloadWidget(payload: BankCard.Payload) {
    val bankCardText = stringResource(
        id = R.string.label_scanned_card_info,
        payload.cardNumber,
        payload.cardType,
        payload.emvRawData
    )

    Text(
        text = bankCardText,
        modifier = DefaultPadding(),
        color = Color.White
    )
}

@Composable
fun BankCardWidget(bankCard: BankCard) {
    val cardNumberLabel = stringResource(
        id = R.string.label_bank_card_number,
        bankCard.displayNumber
    )

    Column {
        Text(
            text = cardNumberLabel,
            color = Color.White
        )

        Text(
            text = bankCard.cardType,
            color = Color.White
        )
    }
}

@Composable
fun PasswordFormWidget(onSubmit: (password: String) -> Unit) {
    Column(
        modifier = DefaultPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var text by remember { mutableStateOf("") }

        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = {
                val passwordLabel = stringResource(id = R.string.label_password)
                Text(passwordLabel)
            },
            colors = TextFieldDefaults.textFieldColors(
                textColor = Color.White,
                placeholderColor = Color.Gray
            )
        )

        OutlinedButton(
            onClick = {
                onSubmit(text)
            },
            modifier = DefaultPadding()
        ) {
            val saveLabel = stringResource(id = R.string.label_save)
            Text(saveLabel)
        }
    }
}