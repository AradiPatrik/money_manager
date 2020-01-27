package com.aradipatrik.yamm.features.add.transaction

import android.os.Bundle
import android.view.View
import com.airbnb.mvrx.BaseMvRxFragment
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import com.aradipatrik.presentation.viewmodels.add.transaction.AddTransactionViewEvent.*
import com.aradipatrik.presentation.viewmodels.add.transaction.AddTransactionViewModel
import com.aradipatrik.presentation.viewmodels.add.transaction.CalculatorState
import com.aradipatrik.yamm.R
import com.aradipatrik.yamm.features.add.transaction.adapter.CategoryAdapter
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.synthetic.main.fragment_calculator_sheet.*
import kotlinx.android.synthetic.main.fragment_calculator_sheet.view.*
import org.koin.android.ext.android.inject

class AddTransactionFragment : BaseMvRxFragment(R.layout.fragment_calculator_sheet) {
    private val viewModel: AddTransactionViewModel by fragmentViewModel()
    private val adapter: CategoryAdapter by inject()

    private val uiEvents
        get() = Observable.merge(
            listOf(
                number_pad_tick.clicks().map { AddClick },
                number_pad_number_0.clicks().map { NumberClick(0) },
                number_pad_number_1.clicks().map { NumberClick(1) },
                number_pad_number_2.clicks().map { NumberClick(2) },
                number_pad_number_3.clicks().map { NumberClick(3) },
                number_pad_number_4.clicks().map { NumberClick(4) },
                number_pad_number_5.clicks().map { NumberClick(5) },
                number_pad_number_6.clicks().map { NumberClick(6) },
                number_pad_number_7.clicks().map { NumberClick(7) },
                number_pad_number_8.clicks().map { NumberClick(8) },
                number_pad_number_9.clicks().map { NumberClick(9) },
                number_pad_point.clicks().map { PointClick },
                number_pad_delete_one.clicks().map { DeleteOneClick },
                number_pad_number_plus.clicks().map { PlusClick },
                number_pad_number_minus.clicks().map { MinusClick }
            )
        )

    private val disposable = CompositeDisposable()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        category_select_recycler_view.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        disposable += uiEvents.subscribe(viewModel::processEvent)
    }

    override fun onPause() {
        super.onPause()
        disposable.clear()
    }

    override fun invalidate() = withState(viewModel) { state ->
        // TODO: Refactor this to use view data
        view?.expression_display?.text = when (val cs = state.calculatorState) {
            is CalculatorState.SingleValue -> cs.value.toString()
            is CalculatorState.AddOperation -> "${cs.lhs} + ${cs.rhs ?: ""}"
            is CalculatorState.SubtractOperation -> "${cs.lhs} - ${cs.rhs ?: ""}"
        }
    }
}