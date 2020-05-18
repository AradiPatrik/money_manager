package com.aradipatrik.yamm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import com.airbnb.mvrx.BaseMvRxFragment
import com.aradipatrik.yamm.MainFragmentUiEffect.BottomSheetCollapse
import com.aradipatrik.yamm.MainFragmentUiEffect.FabClick
import com.aradipatrik.yamm.common.holder.ToolbarHolder
import com.aradipatrik.yamm.common.viewext.asBottomSheet
import com.aradipatrik.yamm.common.viewext.collapseEvents
import com.aradipatrik.yamm.common.viewext.hideAsBottomSheet
import com.aradipatrik.yamm.common.viewext.showAsBottomSheet
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.synthetic.main.fragment_main.calculator_sheet
import kotlinx.android.synthetic.main.fragment_main.fab
import kotlinx.android.synthetic.main.fragment_main.sum_sheet
import kotlinx.android.synthetic.main.fragment_main.toolbar
import kotlinx.android.synthetic.main.sum_sheet.view.sum_sheet_container
import org.koin.android.ext.android.inject

sealed class MainFragmentUiEffect {
    object FabClick : MainFragmentUiEffect()
    object BottomSheetCollapse: MainFragmentUiEffect()
}

class MainFragment : BaseMvRxFragment() {
    private val uiEffectsDisposable = CompositeDisposable()
    private val toolbarHolder: ToolbarHolder by inject()

    private val uiEffects get() = Observable.merge(
        fab.clicks().map { FabClick },
        calculator_sheet.asBottomSheet().collapseEvents().map { BottomSheetCollapse }
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = inflater.inflate(R.layout.fragment_main, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            hideCalculator()
            showFab()
            toolbarHolder.setToolbar(toolbar)
        }
    }

    override fun onResume() {
        super.onResume()
        uiEffectsDisposable += uiEffects.subscribe(::handleUiEffect)
    }

    override fun onPause() {
        super.onPause()
        uiEffectsDisposable.clear()
    }

    private fun handleUiEffect(effect: MainFragmentUiEffect) = when (effect) {
        FabClick -> {
            showCalculator()
            hideFab()
        }
        BottomSheetCollapse -> showFab()
    }

    private fun showCalculator() = calculator_sheet.showAsBottomSheet()

    private fun hideCalculator() = calculator_sheet.hideAsBottomSheet()

    private fun hideSumSheet() = sum_sheet.hideAsBottomSheet()

    private fun showFab() = fab.show()

    private fun hideFab() = fab.hide()

    override fun invalidate() {
    }
}
