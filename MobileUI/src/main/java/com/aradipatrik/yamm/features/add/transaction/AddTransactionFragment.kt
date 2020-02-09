package com.aradipatrik.yamm.features.add.transaction

import android.os.Bundle
import android.view.View
import com.airbnb.mvrx.BaseMvRxFragment
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import com.aradipatrik.presentation.viewmodels.add.transaction.AddTransactionViewEvent.*
import com.aradipatrik.presentation.viewmodels.add.transaction.AddTransactionViewModel
import com.aradipatrik.yamm.R
import com.aradipatrik.yamm.features.add.transaction.adapter.CategoryAdapter
import com.aradipatrik.yamm.features.add.transaction.mapper.CalculatorViewDataMapper
import com.aradipatrik.yamm.features.add.transaction.model.CalculatorAction
import com.jakewharton.rxbinding3.view.clicks
import com.jakewharton.rxbinding3.widget.textChanges
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.synthetic.main.fragment_calculator_sheet.*
import org.koin.android.ext.android.inject

class AddTransactionFragment : BaseMvRxFragment(R.layout.fragment_calculator_sheet) {
    private val viewModel: AddTransactionViewModel by fragmentViewModel()
    private val adapter: CategoryAdapter by inject()
    private val viewDataMapper: CalculatorViewDataMapper by inject()

    private val uiEvents
        get() = Observable.merge(
            listOf(
                number_pad_action.clicks().map { ActionClick },
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
                number_pad_number_minus.clicks().map { MinusClick },
                memo_edit_Text.textChanges()
                    .map { MemoChange(it.toString()) }
                    .distinctUntilChanged()
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
        val vd = viewDataMapper.mapToViewData(state)
        expression_display.text = vd.numberDisplay
        if (vd.calculatorAction === CalculatorAction.CalculateResult) {
            number_pad_action.icon = null
            number_pad_action.text = "="
        } else {
            number_pad_action.icon = context?.getDrawable(R.drawable.ic_check_24dp)
            number_pad_action.text = ""
        }
    }
}