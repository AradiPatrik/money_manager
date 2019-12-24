package com.aradipatrik.data.repository

import com.aradipatrik.data.model.TransactionEntity
import io.reactivex.Completable
import io.reactivex.Observable
import org.joda.time.Interval

interface TransactionDataStore {
    fun getAllTransactions(): Observable<List<TransactionEntity>>
    fun getTransactionsInInterval(interval: Interval): Observable<List<TransactionEntity>>
    fun deleteTransaction(id: String): Completable
    fun addTransaction(transaction: TransactionEntity): Completable
    fun updateTransaction(transaction: TransactionEntity): Completable
}
