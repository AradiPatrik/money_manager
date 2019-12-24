package com.aradipatrik.data.repository

import com.aradipatrik.data.model.TransactionEntity
import io.reactivex.Completable
import io.reactivex.Observable
import org.joda.time.Interval

interface TransactionsDataSource {
    fun getAllTransactions(): Observable<List<TransactionEntity>>
    fun getTransactionsInInterval(interval: Interval): Observable<List<TransactionEntity>>
    fun deleteTransactions(id: String): Completable
    fun addTransaction(transaction: TransactionEntity): Completable
    fun editTransaction(transaction: TransactionEntity): Completable
}
