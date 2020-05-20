package com.aradipatrik.domain.interfaces.data

import com.aradipatrik.domain.model.ExpenseStats
import io.reactivex.Observable
import org.joda.time.YearMonth

interface ExpenseStatsRepository {
    fun getAlltimeExpenseStats(walletId: String): Observable<ExpenseStats>
    fun getExpenseStatsOfMonth(yearMonth: YearMonth, walletId: String): Observable<ExpenseStats>
}