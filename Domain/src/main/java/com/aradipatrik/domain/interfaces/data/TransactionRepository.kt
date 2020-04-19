package com.aradipatrik.domain.interfaces.data

import com.aradipatrik.domain.model.Transaction
import io.reactivex.Completable
import io.reactivex.Observable
import org.joda.time.Interval

interface TransactionRepository {
    fun getInInterval(interval: Interval, walletId: String): Observable<List<Transaction>>
    fun add(transaction: Transaction, walletId: String): Completable
    fun update(transaction: Transaction, walletId: String): Completable
    fun delete(id: String): Completable
}
