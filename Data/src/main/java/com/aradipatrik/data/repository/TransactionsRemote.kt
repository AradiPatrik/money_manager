package com.aradipatrik.data.repository

import com.aradipatrik.data.model.TransactionEntity
import io.reactivex.Completable

interface TransactionsRemote {
    fun getAllTransactions(): List<TransactionEntity>
    fun getTransactionsAfter(timestamp: Long): List<TransactionEntity>
    fun updateWithOfflineTransactions(transactions: List<TransactionEntity>): Completable
}
