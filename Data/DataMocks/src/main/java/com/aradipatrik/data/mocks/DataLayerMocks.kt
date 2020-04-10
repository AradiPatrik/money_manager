package com.aradipatrik.data.mocks

import com.aradipatrik.data.mapper.SyncStatus
import com.aradipatrik.data.model.CategoryEntity
import com.aradipatrik.data.model.TransactionWithCategory
import com.aradipatrik.data.model.TransactionWithIds
import com.aradipatrik.data.model.WalletEntity
import com.aradipatrik.testing.CommonMocks.date
import com.aradipatrik.testing.CommonMocks.int
import com.aradipatrik.testing.CommonMocks.long
import com.aradipatrik.testing.CommonMocks.string
import org.joda.time.DateTime

@SuppressWarnings("LongParameterList") // As these are mock factory methods
object DataLayerMocks {
    fun categoryEntity(
        id: String = string(),
        name: String = string(),
        iconId: String = string(),
        walletId: String = string(),
        updatedTimeStamp: Long = long(),
        syncStatus: SyncStatus = SyncStatus.None
    ) = CategoryEntity(
        id = id,
        name = name,
        iconId = iconId,
        updatedTimeStamp = updatedTimeStamp,
        walletId = walletId,
        syncStatus = syncStatus
    )

    fun transactionWithCategory(
        id: String = string(),
        category: CategoryEntity = categoryEntity(),
        amount: Int = int(),
        walletId: String = string(),
        memo: String = string(),
        date: DateTime = date(),
        updatedTimeStamp: Long = long(),
        syncStatus: SyncStatus = SyncStatus.None
    ) = TransactionWithCategory(
        id = id,
        category = category,
        amount = amount,
        memo = memo,
        date = date,
        walletId = walletId,
        updatedTimeStamp = updatedTimeStamp,
        syncStatus = syncStatus
    )

    fun transactionWithIds(
        id: String = string(),
        categoryId: String = string(),
        walletId: String = string(),
        amount: Int = int(),
        memo: String = string(),
        date: DateTime = date(),
        updatedTimeStamp: Long = long(),
        syncStatus: SyncStatus = SyncStatus.None
    ) = TransactionWithIds(
        id = id,
        categoryId = categoryId,
        walletId = walletId,
        amount = amount,
        memo = memo,
        date = date,
        updatedTimeStamp = updatedTimeStamp,
        syncStatus = syncStatus
    )

    fun walletEntity(
        id: String = string(),
        name: String = string(),
        updatedTimeStamp: Long = long(),
        syncStatus: SyncStatus = SyncStatus.None
    ) = WalletEntity(
        id = id,
        name = name,
        updatedTimeStamp = updatedTimeStamp,
        syncStatus = syncStatus
    )
}
