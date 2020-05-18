package com.aradipatrik.domain.interfaces.data

import io.reactivex.Completable
import io.reactivex.Observable
import org.joda.time.DateTime

interface SelectedMonthRepository {
    fun setSelectedMonth(dateTime: DateTime): Completable
    fun getSelectedMonth(): Observable<DateTime>
}
