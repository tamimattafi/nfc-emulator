package com.attafitamim.nfc.data.utils

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

fun <T> T.serializeToJson(): String =
    jacksonObjectMapper().writeValueAsString(this)

fun <T> T.serializeToJsonBytes(): ByteArray =
    jacksonObjectMapper().writeValueAsBytes(this)

fun <T> String.readFromJson(clazz: Class<T>): T =
    jacksonObjectMapper().readValue(this, clazz)

fun <T> ByteArray.readFromJson(clazz: Class<T>): T =
    jacksonObjectMapper().readValue(this, clazz)