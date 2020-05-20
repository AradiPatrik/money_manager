package com.aradipatrik.domain.interactor.selectedmonth

import com.aradipatrik.domain.interfaces.data.SelectedMonthRepository
import com.aradipatrik.domain.usecase.ObservableUseCase
import org.joda.time.DateTime
import org.joda.time.YearMonth

class GetSelectedMonthInteractor(
    private val selectedMonthRepository: SelectedMonthRepository
): ObservableUseCase<YearMonth, Unit> {
    override fun get(params: Unit?) = selectedMonthRepository.getSelectedMonth()
}
