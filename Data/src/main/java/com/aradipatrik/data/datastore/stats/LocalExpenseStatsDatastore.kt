package com.aradipatrik.data.datastore.stats

import io.reactivex.Observable
import org.joda.time.YearMonth

interface LocalExpenseStatsDatastore {

    fun getAllTimeIncome(walletId: String): Observable<Int>
    fun getAllTimeExpense(walletId: String): Observable<Int>

    fun getMonthlyIncome(yearMonth: YearMonth, walletId: String): Observable<Int>
    fun getMonthlyExpense(yearMonth: YearMonth, walletId: String): Observable<Int>
}