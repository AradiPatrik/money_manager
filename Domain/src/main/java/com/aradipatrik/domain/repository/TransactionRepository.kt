package com.aradipatrik.domain.repository

import com.aradipatrik.domain.model.Transaction
import io.reactivex.Completable
import io.reactivex.Observable
import org.joda.time.Interval

interface TransactionRepository {
    fun getAllTransactions(): Observable<List<Transaction>>
    fun getTransactionsInInterval(interval: Interval): Observable<List<Transaction>>
    fun addTransaction(transaction: Transaction): Completable
    fun updateTransaction(transaction: Transaction): Completable
    fun deleteTransaction(id: String): Completable
}