package com.aradipatrik.yamm.features.sumsheet.view

import com.airbnb.mvrx.BaseMvRxFragment
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import com.aradipatrik.presentation.viewmodels.sumsheet.SumSheetViewEvent
import com.aradipatrik.presentation.viewmodels.sumsheet.SumSheetViewModel
import com.aradipatrik.yamm.R
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.synthetic.main.sum_sheet.expense_amount_text_view
import kotlinx.android.synthetic.main.sum_sheet.income_amount_text_view
import kotlinx.android.synthetic.main.sum_sheet.left_chevron
import kotlinx.android.synthetic.main.sum_sheet.monthly_amount_text_view
import kotlinx.android.synthetic.main.sum_sheet.right_chevron
import kotlinx.android.synthetic.main.sum_sheet.total_amount_text_view

class SumSheetFragment: BaseMvRxFragment(R.layout.sum_sheet) {
    private val viewEventsDisposable = CompositeDisposable()

    private val viewEvents get() =
        Observable.merge(
            left_chevron.clicks().map { SumSheetViewEvent.DecrementClick },
            right_chevron.clicks().map { SumSheetViewEvent.IncrementClick }
        )

    private val viewModel: SumSheetViewModel by fragmentViewModel()

    override fun onResume() {
        super.onResume()
        viewEventsDisposable += viewEvents.subscribe {
            viewModel.process(it)
        }
    }

    override fun onPause() {
        super.onPause()
        viewEventsDisposable.clear()
    }

    override fun invalidate() = withState(viewModel) { state ->
        total_amount_text_view.text = state.grandTotal.toString()
        monthly_amount_text_view.text = state.monthlyTotal.toString()
        expense_amount_text_view.text = state.expenseThisMonth.toString()
        income_amount_text_view.text = state.incomeThisMonth.toString()
    }
}
