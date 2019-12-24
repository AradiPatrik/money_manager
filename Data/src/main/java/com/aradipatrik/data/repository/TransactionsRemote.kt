package com.aradipatrik.data.repository

import com.aradipatrik.data.model.TransactionEntity
import io.reactivex.Completable
import io.reactivex.Observable

interface TransactionsRemote {
    fun getAllTransactions(): Observable<List<TransactionEntity>>
    fun getTransactionsAfter(timestamp: Long): Observable<List<TransactionEntity>>
    fun updateWithOfflineTransactions(transactions: List<TransactionEntity>): Completable
}
