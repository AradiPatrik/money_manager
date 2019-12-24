package com.aradipatrik.data.store

import com.aradipatrik.data.model.TransactionEntity
import com.aradipatrik.data.repository.TransactionDataStore
import com.aradipatrik.data.repository.TransactionsRemote
import io.reactivex.Completable
import io.reactivex.Observable
import org.joda.time.Interval
import javax.inject.Inject

class TransactionsRemoteDataStore @Inject constructor(
    private val transactionsRemote: TransactionsRemote
) : TransactionDataStore {
    override fun getAllTransactions(): Observable<List<TransactionEntity>> {
        return transactionsRemote.getAllTransactions()
    }

    override fun getTransactionsInInterval(interval: Interval): Observable<List<TransactionEntity>> {
        throw UnsupportedOperationException(
            "Remote transaction data store does not support getting all transactions in interval"
        )
    }

    override fun deleteTransaction(id: String): Completable {
        throw UnsupportedOperationException(
            "Remote transaction data store does not support deleting"
        )
    }

    override fun addTransaction(transaction: TransactionEntity): Completable {
        throw UnsupportedOperationException(
            "Remote transaction data store does not support adding transactions"
        )
    }

    override fun updateTransaction(transaction: TransactionEntity): Completable {
        throw UnsupportedOperationException(
            "Remote transaction data store does not support updating transactions"
        )
    }

}