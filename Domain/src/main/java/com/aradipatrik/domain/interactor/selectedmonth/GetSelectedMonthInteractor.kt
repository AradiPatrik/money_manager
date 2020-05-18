package com.aradipatrik.domain.interactor.selectedmonth

import com.aradipatrik.domain.interfaces.data.SelectedMonthRepository
import com.aradipatrik.domain.usecase.ObservableUseCase
import org.joda.time.DateTime

class GetSelectedMonthInteractor(
    val selectedMonthRepository: SelectedMonthRepository
): ObservableUseCase<DateTime, Unit> {
    override fun get(params: Unit?) = selectedMonthRepository.getSelectedMonth()
}
