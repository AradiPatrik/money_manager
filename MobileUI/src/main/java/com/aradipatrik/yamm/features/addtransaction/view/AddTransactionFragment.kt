package com.aradipatrik.yamm.features.addtransaction.view

import android.os.Bundle
import android.view.View
import com.airbnb.mvrx.BaseMvRxFragment
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import com.aradipatrik.presentation.viewmodels.addtransaction.AddTransactionViewEvent.*
import com.aradipatrik.presentation.viewmodels.addtransaction.AddTransactionViewModel
import com.aradipatrik.yamm.R
import com.aradipatrik.yamm.features.addtransaction.mapper.CalculatorViewDataMapper
import com.aradipatrik.yamm.features.addtransaction.mapper.CategoryItemViewDataMapper
import com.aradipatrik.yamm.features.addtransaction.model.CalculatorAction
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
    private val calculatorViewDataMapper: CalculatorViewDataMapper by inject()
    private val categoryItemViewDataMapper: CategoryItemViewDataMapper by inject()

    private val uiEvents
        get() = Observable.merge(
            listOf(
                number_pad_action.clicks().map { ActionClick },
                number_pad_number_0.clicks().map { NumberClick(number = 0) },
                number_pad_number_1.clicks().map { NumberClick(number = 1) },
                number_pad_number_2.clicks().map { NumberClick(number = 2) },
                number_pad_number_3.clicks().map { NumberClick(number = 3) },
                number_pad_number_4.clicks().map { NumberClick(number = 4) },
                number_pad_number_5.clicks().map { NumberClick(number = 5) },
                number_pad_number_6.clicks().map { NumberClick(number = 6) },
                number_pad_number_7.clicks().map { NumberClick(number = 7) },
                number_pad_number_8.clicks().map { NumberClick(number = 8) },
                number_pad_number_9.clicks().map { NumberClick(number = 9) },
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
        val calculatorViewData = calculatorViewDataMapper.mapToViewData(state)
        expression_display.text = calculatorViewData.numberDisplay
        if (calculatorViewData.calculatorAction === CalculatorAction.CalculateResult) {
            number_pad_action.icon = null
            number_pad_action.text = "="
        } else {
            number_pad_action.icon = context?.getDrawable(R.drawable.ic_check_24dp)
            number_pad_action.text = ""
        }
        adapter.submitList(state.categoryListModel.map {
            categoryItemViewDataMapper.mapToItemViewData(it, it == state.selectedCategoryModel)
        })
    }
}
