package com.attafitamim.nfc.common.utils

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

val <T> T.asJson: String get() =
    jacksonObjectMapper().writeValueAsString(this)

val <T> T.asJsonBytes: ByteArray get() =
    jacksonObjectMapper().writeValueAsBytes(this)

fun <T> String.readFromJson(clazz: Class<T>): T =
    jacksonObjectMapper().readValue(this, clazz)

fun <T> ByteArray.readFromJson(clazz: Class<T>): T =
    jacksonObjectMapper().readValue(this, clazz)