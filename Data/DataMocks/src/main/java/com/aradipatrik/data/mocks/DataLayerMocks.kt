package com.aradipatrik.data.mocks

import com.aradipatrik.data.mapper.SyncStatus
import com.aradipatrik.data.model.CategoryEntity
import com.aradipatrik.data.model.TransactionJoinedEntity
import com.aradipatrik.data.model.TransactionPartialEntity
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
        updatedTimeStamp: Long = long(),
        syncStatus: SyncStatus = SyncStatus.None
    ) = CategoryEntity(
        id = id,
        name = name,
        iconId = iconId,
        updatedTimeStamp = updatedTimeStamp,
        syncStatus = syncStatus
    )

    fun transactionJoinedEntity(
        id: String = string(),
        category: CategoryEntity = categoryEntity(),
        amount: Int = int(),
        memo: String = string(),
        date: DateTime = date(),
        updatedTimeStamp: Long = long(),
        syncStatus: SyncStatus = SyncStatus.None
    ) = TransactionJoinedEntity(
        id = id,
        category = category,
        amount = amount,
        memo = memo,
        date = date,
        updatedTimeStamp = updatedTimeStamp,
        syncStatus = syncStatus
    )

    fun transactionPartialEntity(
        id: String = string(),
        categoryId: String = string(),
        amount: Int = int(),
        memo: String = string(),
        date: DateTime = date(),
        updatedTimeStamp: Long = long(),
        syncStatus: SyncStatus = SyncStatus.None
    ) = TransactionPartialEntity(
        id = id,
        categoryId = categoryId,
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
