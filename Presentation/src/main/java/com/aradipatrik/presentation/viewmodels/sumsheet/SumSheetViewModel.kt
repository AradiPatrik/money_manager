package com.aradipatrik.presentation.viewmodels.sumsheet

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.Uninitialized
import com.airbnb.mvrx.ViewModelContext
import com.aradipatrik.domain.interactor.onboard.LogInWithEmailAndPasswordInteractor
import com.aradipatrik.domain.interactor.onboard.SignUpWithEmailAndPasswordInteractor
import com.aradipatrik.domain.interactor.selectedmonth.DecrementSelectedMonthInteractor
import com.aradipatrik.domain.interactor.selectedmonth.GetSelectedMonthInteractor
import com.aradipatrik.domain.interactor.selectedmonth.IncrementSelectedMonthInteractor
import com.aradipatrik.domain.interactor.stats.GetAllTimeExpenseStatsInteractor
import com.aradipatrik.domain.interactor.stats.GetMonthlyExpenseStatsInteractor
import com.aradipatrik.domain.interactor.stats.GetMonthlyExpenseStatsInteractor.Params.Companion.forSelectedMonth
import com.aradipatrik.presentation.common.MvRxViewModel
import com.aradipatrik.presentation.viewmodels.onboarding.OnboardingState
import com.aradipatrik.presentation.viewmodels.onboarding.OnboardingViewModel
import org.joda.time.YearMonth
import org.koin.android.ext.android.inject

data class SumSheetState(
    val getSelectedMonthOperation: Async<YearMonth> = Uninitialized,
    val selectedMonth: YearMonth? = null,
    val incomeThisMonth: Int = 0,
    val expenseThisMonth: Int = 0,
    val grandTotal: Int = 0,
    val monthlyTotal: Int = 0
) : MvRxState

class SumSheetViewModel(
    initialState: SumSheetState,
    getSelectedMonthInteractor: GetSelectedMonthInteractor,
    private val incrementSelectedMonthInteractor: IncrementSelectedMonthInteractor,
    private val decrementSelectedMonthInteractor: DecrementSelectedMonthInteractor,
    getAllTimeExpenseStatsInteractor: GetAllTimeExpenseStatsInteractor,
    getMonthlyExpenseStatsInteractor: GetMonthlyExpenseStatsInteractor
) : MvRxViewModel<SumSheetState>(initialState) {

    companion object : MvRxViewModelFactory<SumSheetViewModel, SumSheetState> {
        override fun create(
            viewModelContext: ViewModelContext,
            state: SumSheetState
        ) = SumSheetViewModel(
            state,
            viewModelContext.activity.inject<GetSelectedMonthInteractor>().value,
            viewModelContext.activity.inject<IncrementSelectedMonthInteractor>().value,
            viewModelContext.activity.inject<DecrementSelectedMonthInteractor>().value,
            viewModelContext.activity.inject<GetAllTimeExpenseStatsInteractor>().value,
            viewModelContext.activity.inject<GetMonthlyExpenseStatsInteractor>().value
        )
    }

    init {
        val selectedMonthStream = getSelectedMonthInteractor.get()
            .publish()
            .refCount()

        selectedMonthStream
            .execute {
                copy(
                    getSelectedMonthOperation = it,
                    selectedMonth = it() ?: selectedMonth
                )
            }

        selectedMonthStream
            .flatMap {
                getMonthlyExpenseStatsInteractor.get(forSelectedMonth(it))
            }
            .execute {
                copy(
                    incomeThisMonth = it()?.income ?: incomeThisMonth,
                    expenseThisMonth = it()?.expense ?: expenseThisMonth,
                    monthlyTotal = it()?.total ?: monthlyTotal
                )
            }

        getAllTimeExpenseStatsInteractor.get().execute {
            copy(grandTotal = it()?.total ?: grandTotal)
        }
    }

    fun process(viewEvent: SumSheetViewEvent) = when(viewEvent) {
        SumSheetViewEvent.DecrementClick -> incrementSelectedMonthInteractor.get().execute { this }
        SumSheetViewEvent.IncrementClick -> decrementSelectedMonthInteractor.get().execute { this }
    }
}