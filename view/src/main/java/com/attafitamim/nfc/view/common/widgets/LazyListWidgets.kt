package com.attafitamim.nfc.view.common.widgets

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.ui.Modifier
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.attafitamim.nfc.view.common.styles.DefaultPadding

internal fun LazyListScope.LoadingPage() {
    item {
        val matchParent = DefaultPadding().fillParentMaxSize()
        CircularProgressIndicator(modifier = matchParent)
    }
}

internal fun LazyListScope.LoadingItem() {
    item {
        val padding = DefaultPadding()
        CircularProgressIndicator(modifier = padding)
    }
}

internal fun LazyListScope.ErrorItem(error: LoadState.Error) {
    item {
        val errorLabel = error.error.localizedMessage ?: error.toString()
        val padding = DefaultPadding()
        ErrorLabel(text = errorLabel, modifier = padding)
    }
}

internal fun LazyListScope.ErrorPage(error: LoadState.Error) {
    item {
        val errorLabel = error.error.localizedMessage ?: error.toString()
        val matchParent = Modifier.fillParentMaxSize()
        ErrorLabel(text = errorLabel, modifier = matchParent)
    }
}

internal fun LazyListScope.StateItem(lazyItems: LazyPagingItems<*>?) {
    if (lazyItems == null) {
        LoadingPage()
        return
    }

    val loadState = lazyItems.loadState
    when {
        loadState.refresh is LoadState.Loading -> LoadingPage()
        loadState.append is LoadState.Loading -> LoadingItem()

        loadState.refresh is LoadState.Error -> {
            val error = lazyItems.loadState.refresh as LoadState.Error
            ErrorPage(error = error)
        }

        loadState.append is LoadState.Error -> {
            val error = lazyItems.loadState.refresh as LoadState.Error
            ErrorItem(error = error)
        }
    }
}