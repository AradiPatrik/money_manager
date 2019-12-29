package com.aradipatrik.domain.repository

import com.aradipatrik.domain.model.Transaction
import io.reactivex.Completable
import io.reactivex.Observable
import org.joda.time.Interval

interface TransactionRepository {
    fun getInInterval(interval: Interval): Observable<List<Transaction>>
    fun add(transaction: Transaction): Completable
    fun update(transaction: Transaction): Completable
    fun delete(id: String): Completable
}