package com.aradipatrik.domain.interactor.selectedmonth

import com.aradipatrik.domain.interfaces.data.SelectedMonthRepository
import com.aradipatrik.domain.usecase.CompletableUseCase
import org.joda.time.Period

class DecrementSelectedMonthInteractor(
    private val selectedMonthRepository: SelectedMonthRepository
): CompletableUseCase<Unit> {
    override fun get(params: Unit?) = selectedMonthRepository.getSelectedMonth()
        .map { it.minus(Period.months(1)) }
        .flatMapCompletable { selectedMonthRepository.setSelectedMonth(it) }
}
