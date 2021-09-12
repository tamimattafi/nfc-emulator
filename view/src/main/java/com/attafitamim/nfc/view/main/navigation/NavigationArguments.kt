package com.attafitamim.nfc.view.main.navigation

import androidx.navigation.compose.NamedNavArgument

enum class NavigationArguments(val key: String) {
    TASK_ID(key = "task_id") {
        override val argument get() = intArgument(key)
    };

    abstract val argument: NamedNavArgument
}