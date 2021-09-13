package com.attafitamim.nfc.view.destinations.cards.list.source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.attafitamim.nfc.domain.model.cards.BankCard
import com.attafitamim.nfc.domain.usecase.cards.storage.GetBankCards

class BankCardsSource(
    private val getTasksPage: GetBankCards
) : PagingSource<Int, BankCard>() {

    override fun getRefreshKey(state: PagingState<Int, BankCard>) = START_PAGE_NUMBER

    override suspend fun load(params: LoadParams<Int>) =
        try {
            val currentPage = params.key ?: START_PAGE_NUMBER
            val bankCardsList = getTasksPage(currentPage)

            val previousPage = if (currentPage == START_PAGE_NUMBER) null
            else currentPage - DEFAULT_PAGE_STEP

            val nextPage = currentPage + DEFAULT_PAGE_STEP

            LoadResult.Page(
                data = bankCardsList,
                prevKey = previousPage,
                nextKey = nextPage.plus(DEFAULT_PAGE_STEP)
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }

    private companion object {
        const val START_PAGE_NUMBER = 1
        const val DEFAULT_PAGE_STEP = 1
    }
}