package com.aradipatrik.yamm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.airbnb.mvrx.BaseMvRxFragment
import com.aradipatrik.yamm.MainFragmentUiEffect.FabClick
import com.aradipatrik.yamm.common.viewext.hideAsBottomSheet
import com.aradipatrik.yamm.common.viewext.showAsBottomSheet
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.synthetic.main.fragment_main.*

sealed class MainFragmentUiEffect {
    object FabClick: MainFragmentUiEffect()
}

class MainFragment : BaseMvRxFragment() {
    private val uiEffectsDisposable = CompositeDisposable()

    private val uiEffects get() = fab.clicks().map { FabClick }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onResume() {
        super.onResume()
//        uiEffectsDisposable += uiEffects.subscribe(::handleUiEffect)
    }

    override fun onPause() {
        super.onPause()
        uiEffectsDisposable.clear()
    }

//    private fun handleUiEffect(effect: MainFragmentUiEffect) = when(effect) {
//        FabClick -> TODO()
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
//            hideCalculator()
            hideSumSheet()
        }
    }

//    private fun showCalculator() = calculator_sheet.showAsBottomSheet()
//
//    private fun hideCalculator() = calculator_sheet.hideAsBottomSheet()

    private fun showSumSheet() = sum_sheet_container.showAsBottomSheet()

    private fun hideSumSheet() = sum_sheet_container.hideAsBottomSheet()

    override fun invalidate()  {

    }

}