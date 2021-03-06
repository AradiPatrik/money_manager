package com.aradipatrik.yamm.features.history.view

import android.os.Bundle
import android.view.View
import com.airbnb.mvrx.BaseMvRxFragment
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import com.aradipatrik.presentation.presentations.TransactionPresentationModel
import com.aradipatrik.presentation.viewmodels.history.TransactionHistoryViewModel
import com.aradipatrik.yamm.R
import com.aradipatrik.yamm.common.holder.ToolbarHolder
import com.aradipatrik.yamm.features.history.mapper.TransactionViewDataMapper
import kotlinx.android.synthetic.main.fragment_history.view.*
import org.joda.time.LocalDate
import org.koin.android.ext.android.inject
import java.util.*

class HistoryFragment : BaseMvRxFragment(R.layout.fragment_history) {

    private val viewModel: TransactionHistoryViewModel by fragmentViewModel()
    private val viewDataMapper: TransactionViewDataMapper by inject()
    private val adapter: HistoryAdapter by inject()
    private val toolbarHolder: ToolbarHolder by inject()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.history_recycler_view.adapter = adapter
        toolbarHolder.setTitle(getString(R.string.label_history))
    }

    override fun invalidate() = withState(viewModel) { state ->
        adapter.submitList(flattenDateCategorisedItemsToViewDataList(state.datesToTransactions))
    }

    private fun flattenDateCategorisedItemsToViewDataList(
        items: SortedMap<LocalDate, SortedSet<TransactionPresentationModel>>
    ) = items.flatMap { (date, transactionsInDate) ->
        listOf(viewDataMapper.mapToHeaderViewData(date)) +
                transactionsInDate.map(viewDataMapper::mapToItemViewData)
    }
}
