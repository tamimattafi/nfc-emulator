package com.attafitamim.nfc.view.destinations.cards.list.view

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.attafitamim.nfc.domain.model.cards.BankCard
import com.attafitamim.nfc.view.R
import com.attafitamim.nfc.view.common.styles.DefaultPadding
import com.attafitamim.nfc.view.common.widgets.StateItem
import com.attafitamim.nfc.view.destinations.cards.global.BankCardWidget
import com.attafitamim.nfc.view.destinations.cards.list.model.BankCardsListSideEffect
import com.attafitamim.nfc.view.destinations.cards.list.model.BankCardsListViewModel
import com.attafitamim.nfc.view.navigation.NavigationDestination.Companion.toDestination
import kotlinx.coroutines.flow.collect

@Composable
fun BankCardsListWidget(
    viewModel: BankCardsListViewModel,
    navController: NavHostController
) {
    SideEffectsContainer(viewModel = viewModel, navController = navController)

    val state = viewModel.container.stateFlow.collectAsState().value
    val lazyCards = state.cardsFlow?.collectAsLazyPagingItems()
    BankCardsList(lazyCards = lazyCards, viewModel = viewModel)
}

@Composable
fun SideEffectsContainer(
    viewModel: BankCardsListViewModel,
    navController: NavHostController
) = LaunchedEffect(viewModel) {
    viewModel.container.sideEffectFlow.collect { sideEffect ->
        when (sideEffect) {
            is BankCardsListSideEffect.OpenDestination -> {
                navController.toDestination(sideEffect.destination)
            }
        }
    }
}

@Composable
private fun BankCardsList(
    lazyCards: LazyPagingItems<BankCard>?,
    viewModel: BankCardsListViewModel,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            OutlinedButton(
                onClick = {
                    viewModel.openCardScan()
                },
                modifier = DefaultPadding()
            ) {
                val scanLabel = stringResource(id = R.string.label_scan_card)
                Text(scanLabel)
            }
        }

        item {
            OutlinedButton(
                onClick = {
                    viewModel.openCardCreation()
                },
                modifier = DefaultPadding()
            ) {
                val addLabel = stringResource(id = R.string.label_add_card)
                Text(addLabel)
            }
        }

        if (lazyCards != null) {
            items(items = lazyCards, key = BankCard::id) { card ->
                requireNotNull(card)
                BankCardItem(
                    bankCard = card,
                    onCardClick = {
                        viewModel.openBankCardDetails(card)
                    }
                )
            }
        }

        StateItem(lazyItems = lazyCards)
    }
}

@Composable
private fun BankCardItem(bankCard: BankCard, onCardClick: () -> Unit) {
    TextButton(
        onClick = onCardClick,
        modifier = DefaultPadding().fillMaxWidth()
    ) {
        BankCardWidget(bankCard = bankCard)
    }
}