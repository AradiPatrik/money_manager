package com.aradipatrik.domain.interactor.selectedmonth

import com.aradipatrik.domain.interfaces.data.SelectedMonthRepository
import com.aradipatrik.domain.usecase.CompletableUseCase
import io.reactivex.Completable
import org.joda.time.Period

class IncrementSelectedMonthInteractor(
    val selectedMonthRepository: SelectedMonthRepository
) : CompletableUseCase<Unit> {
    override fun get(params: Unit?) = selectedMonthRepository.getSelectedMonth()
        .take(1)
        .map { it.plus(Period.months(1)) }
        .flatMapCompletable { selectedMonthRepository.setSelectedMonth(it) }

}