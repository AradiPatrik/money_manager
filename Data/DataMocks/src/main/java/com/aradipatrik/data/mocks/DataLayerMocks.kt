package com.aradipatrik.data.mocks

import com.aradipatrik.data.mapper.SyncStatus
import com.aradipatrik.data.model.CategoryDataModel
import com.aradipatrik.data.model.TransactionWithCategoryDataModel
import com.aradipatrik.data.model.TransactionWithIdsDataModel
import com.aradipatrik.data.model.WalletDataModel
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
    ) = CategoryDataModel(
        id = id,
        name = name,
        iconId = iconId,
        updatedTimeStamp = updatedTimeStamp,
        walletId = walletId,
        syncStatus = syncStatus
    )

    fun transactionWithCategory(
        id: String = string(),
        category: CategoryDataModel = categoryEntity(),
        amount: Int = int(),
        walletId: String = string(),
        memo: String = string(),
        date: DateTime = date(),
        updatedTimeStamp: Long = long(),
        syncStatus: SyncStatus = SyncStatus.None
    ) = TransactionWithCategoryDataModel(
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
    ) = TransactionWithIdsDataModel(
        id = id,
        categoryId = categoryId,
        walletId = walletId,
        amount = amount,
        memo = memo,
        date = date,
        updatedTimeStamp = updatedTimeStamp,
        syncStatus = syncStatus
    )

    fun walletDataModel(
        id: String = string(),
        name: String = string(),
        updatedTimeStamp: Long = long(),
        syncStatus: SyncStatus = SyncStatus.None
    ) = WalletDataModel(
        id = id,
        name = name,
        updatedTimeStamp = updatedTimeStamp,
        syncStatus = syncStatus
    )
}
