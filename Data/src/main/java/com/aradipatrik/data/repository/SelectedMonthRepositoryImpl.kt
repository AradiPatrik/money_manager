package com.aradipatrik.data.repository

import com.aradipatrik.domain.interfaces.data.SelectedMonthRepository
import io.reactivex.Completable
import io.reactivex.subjects.BehaviorSubject
import org.joda.time.DateTime

class SelectedMonthRepositoryImpl: SelectedMonthRepository {
    private val selectedMonth = BehaviorSubject.createDefault(DateTime.now().monthOfYear().dateTime)

    override fun setSelectedMonth(dateTime: DateTime) = Completable.fromAction {
        selectedMonth.onNext(dateTime)
    }

    override fun getSelectedMonth() = selectedMonth
}
