package com.aradipatrik.data.model

import com.aradipatrik.data.mapper.SyncStatus
import org.joda.time.DateTime

data class TransactionWithIdsDataModel(
    val id: String, val categoryId: String, val walletId: String,
    val amount: Int, val memo: String, val date: DateTime,
    val updatedTimeStamp: Long, val syncStatus: SyncStatus
)