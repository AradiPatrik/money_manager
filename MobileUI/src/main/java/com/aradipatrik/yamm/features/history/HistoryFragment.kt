package com.aradipatrik.yamm.features.history

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.airbnb.mvrx.BaseMvRxFragment
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import com.aradipatrik.presentation.DashboardViewModel
import com.aradipatrik.presentation.presentations.TransactionPresentation
import com.aradipatrik.yamm.R
import com.aradipatrik.yamm.features.history.adapter.HistoryAdapter
import com.aradipatrik.yamm.features.history.mapper.TransactionViewDataMapper
import com.aradipatrik.yamm.features.history.model.TransactionViewData
import kotlinx.android.synthetic.main.fragment_history.view.*
import org.joda.time.LocalDate
import org.koin.android.ext.android.inject
import java.util.*

class HistoryFragment : BaseMvRxFragment() {

    private val viewModel: DashboardViewModel by fragmentViewModel()
    private val viewDataMapper: TransactionViewDataMapper by inject()
    private val adapter: HistoryAdapter by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.history_recycler_view.adapter = adapter
    }

    override fun invalidate() = withState(viewModel) { state ->
        adapter.submitList(flattenDateCategorisedItemsToViewDataList(state.datesToTransactions))
    }

    private fun flattenDateCategorisedItemsToViewDataList(
        items: SortedMap<LocalDate, SortedSet<TransactionPresentation>>
    ) = items.flatMap { (date, transactionsInDate) ->
        listOf(
            viewDataMapper.mapToHeaderViewData(date),
            *transactionsInDate.map(viewDataMapper::mapToItemViewData).toTypedArray()
        )
    }
}