package com.aradipatrik.domain.interfaces.data

import io.reactivex.Completable
import io.reactivex.Observable
import org.joda.time.DateTime
import org.joda.time.YearMonth

interface SelectedMonthRepository {
    fun setSelectedMonth(dateTime: YearMonth): Completable
    fun getSelectedMonth(): Observable<YearMonth>
}
