package com.aradipatrik.data.store

import com.aradipatrik.data.model.TransactionEntity
import com.aradipatrik.data.repository.TransactionDataStore
import com.aradipatrik.data.repository.TransactionsLocal
import io.reactivex.Completable
import io.reactivex.Observable
import org.joda.time.Interval
import javax.inject.Inject

class TransactionsLocalDataStore @Inject constructor(
    private val transactionsLocal: TransactionsLocal
) : TransactionDataStore {
    override fun getAllTransactions(): Observable<List<TransactionEntity>> {
        return transactionsLocal.getAllTransactions()
    }

    override fun getTransactionsInInterval(interval: Interval): Observable<List<TransactionEntity>> {
        return transactionsLocal.getTransactionsInInterval(interval)
    }

    override fun deleteTransaction(id: String): Completable {
        return transactionsLocal.deleteTransaction(id)
    }

    override fun addTransaction(transaction: TransactionEntity): Completable {
        return transactionsLocal.addTransaction(transaction)
    }

    override fun updateTransaction(transaction: TransactionEntity): Completable {
        return transactionsLocal.updateTransaction(transaction)
    }

}