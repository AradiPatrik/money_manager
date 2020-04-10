package com.aradipatrik.data.model

import com.aradipatrik.data.mapper.SyncStatus
import org.joda.time.DateTime

data class TransactionWithCategory(
    val id: String, val category: CategoryEntity, val walletId: String,
    val amount: Int, val memo: String, val date: DateTime,
    val updatedTimeStamp: Long, val syncStatus: SyncStatus
)

data class TransactionWithIds(
    val id: String, val categoryId: String, val walletId: String,
    val amount: Int, val memo: String, val date: DateTime,
    val updatedTimeStamp: Long, val syncStatus: SyncStatus
)

data class CategoryEntity(
    val id: String, val name: String, val iconId: String, val walletId: String,
    val updatedTimeStamp: Long, val syncStatus: SyncStatus
)

data class WalletEntity(
    val id: String, val name: String,
    val updatedTimeStamp: Long, val syncStatus: SyncStatus
)
