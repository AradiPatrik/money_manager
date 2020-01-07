package com.aradipatrik.local.model

import androidx.room.Query

interface TransactionDao {
    companion object Queries {
        const val GET_ALL_TRANSACTIONS = "SELECT * FROM ${TransactionConstants.TABLE_NAME}"
        const val GET_PENDING_TRANSACTIONS = "SELECT * FROM ${TransactionConstants.TABLE_NAME}" +
                "WHERE ${TransactionConstants.IS_PENDING} is true"
    }

    @Query(Queries.GET_ALL_TRANSACTIONS)
    fun getAllTransactions(): TransactionRow

    @Query(Queries.GET_PENDING_TRANSACTIONS)
    fun getPendingTransactions(): TransactionRow
}