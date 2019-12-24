package com.aradipatrik.data.repository

import com.aradipatrik.data.model.TransactionEntity
import io.reactivex.Completable
import io.reactivex.Observable
import org.joda.time.Interval


interface TransactionsLocal {
    fun getAllTransactions(): Observable<List<TransactionEntity>>
    fun getTransactionsInInterval(interval: Interval): Observable<List<TransactionEntity>>
    fun addTransaction(transaction: TransactionEntity): Completable
    fun updateTransaction(transaction: TransactionEntity): Completable
    fun deleteTransaction(id: String): Completable

    fun getUnsynchronisedTransactions(): List<TransactionEntity>
    fun setSynchronised(ids: List<String>): Completable
}
