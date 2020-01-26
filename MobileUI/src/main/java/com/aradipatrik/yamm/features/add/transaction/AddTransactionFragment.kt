package com.aradipatrik.yamm.features.add.transaction

import com.airbnb.mvrx.BaseMvRxFragment
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import com.aradipatrik.presentation.viewmodels.add.transaction.AddTransactionViewModel
import com.aradipatrik.yamm.R
import com.aradipatrik.presentation.viewmodels.add.transaction.AddTransactionViewEvent.*
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.synthetic.main.fragment_calculator_sheet.view.*

class AddTransactionFragment : BaseMvRxFragment(R.layout.fragment_calculator_sheet) {
    private val viewModel: AddTransactionViewModel by fragmentViewModel()

    private val uiEvents get() = Observable.merge(listOf(
        view?.number_pad_tick?.clicks()?.map { AddClick },
        view?.number_pad_number_0?.clicks()?.map { NumberClick(0) },
        view?.number_pad_number_1?.clicks()?.map { NumberClick(1) },
        view?.number_pad_number_2?.clicks()?.map { NumberClick(2) },
        view?.number_pad_number_3?.clicks()?.map { NumberClick(3) },
        view?.number_pad_number_4?.clicks()?.map { NumberClick(4) },
        view?.number_pad_number_5?.clicks()?.map { NumberClick(5) },
        view?.number_pad_number_6?.clicks()?.map { NumberClick(6) },
        view?.number_pad_number_7?.clicks()?.map { NumberClick(7) },
        view?.number_pad_number_8?.clicks()?.map { NumberClick(8) },
        view?.number_pad_number_9?.clicks()?.map { NumberClick(9) },
        view?.number_pad_point?.clicks()?.map { PointClick },
        view?.number_pad_delete_one?.clicks()?.map { DeleteOneClick },
        view?.number_pad_number_plus?.clicks()?.map { PlusClick },
        view?.number_pad_number_minus?.clicks()?.map { MinusClick }
    ))

    private val disposable = CompositeDisposable()

    override fun onResume() {
        super.onResume()
        disposable += uiEvents.subscribe()
        TODO("Call viewmodel on viewevents")
    }

    override fun onPause() {
        super.onPause()
        disposable.clear()
    }

    override fun invalidate() = withState(viewModel) {

    }
}