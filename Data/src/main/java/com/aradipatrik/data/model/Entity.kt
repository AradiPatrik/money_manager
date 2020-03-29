package com.aradipatrik.data.model

import com.aradipatrik.data.mapper.SyncStatus
import org.joda.time.DateTime

data class TransactionJoinedEntity(val id: String, val category: CategoryEntity,
                                    val amount: Int, val memo: String, val date: DateTime,
                                    val updatedTimeStamp: Long, val syncStatus: SyncStatus)

data class TransactionPartialEntity(val id: String, val categoryId: String, val amount: Int,
                                    val memo: String, val date: DateTime,
                                    val updatedTimeStamp: Long, val syncStatus: SyncStatus)

data class CategoryEntity(val id: String, val name: String, val iconId: String,
                          val updatedTimeStamp: Long, val syncStatus: SyncStatus)

data class WalletEntity(val id: String, val name: String,
                        val updatedTimeStamp: Long, val syncStatus: SyncStatus)