package com.attafitamim.nfc.view.common.widgets

import androidx.annotation.StringRes
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.attafitamim.nfc.view.common.styles.DefaultPadding

@Composable
fun TextLabel(@StringRes labelId: Int) {
    Text(
        text = stringResource(id = labelId),
        color = Color.White,
        modifier = DefaultPadding(),
    )
}

@Composable
fun ErrorLabel(text: String, modifier: Modifier) {
    Text(
        text = text,
        color = Color.Red,
        modifier = modifier,
    )
}