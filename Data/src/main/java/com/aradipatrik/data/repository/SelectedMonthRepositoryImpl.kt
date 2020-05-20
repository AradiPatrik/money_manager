package com.aradipatrik.data.repository

import com.aradipatrik.domain.interfaces.data.SelectedMonthRepository
import io.reactivex.Completable
import io.reactivex.subjects.BehaviorSubject
import org.joda.time.YearMonth

class SelectedMonthRepositoryImpl: SelectedMonthRepository {
    private val selectedMonth = BehaviorSubject.createDefault(YearMonth.now())

    override fun setSelectedMonth(yearMonth: YearMonth) = Completable.fromAction {
        selectedMonth.onNext(yearMonth)
    }

    override fun getSelectedMonth() = selectedMonth
}
