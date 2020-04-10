package com.aradipatrik.local.mocks

import com.aradipatrik.local.database.category.CategoryRow
import com.aradipatrik.local.database.common.SyncStatusConstants
import com.aradipatrik.local.database.transaction.TransactionRow
import com.aradipatrik.local.database.transaction.TransactionWithCategory
import com.aradipatrik.local.database.wallet.WalletRow
import com.aradipatrik.testing.CommonMocks
import com.aradipatrik.testing.CommonMocks.long
import com.aradipatrik.testing.CommonMocks.string

object LocalDataLayerMocks {
    fun transactionRow(
        uid: String = string(),
        updateTimestamp: Long = long(),
        memo: String = string(),
        amount: Int = CommonMocks.int(),
        date: Long = long(),
        categoryId: String = string(),
        syncStatusCode: Int = SyncStatusConstants.SYNCED_CODE
    ) = TransactionRow(
        uid = uid,
        updateTimestamp = updateTimestamp,
        memo = memo,
        date = date,
        amount = amount,
        categoryId = categoryId,
        syncStatusCode = syncStatusCode
    )

    fun categoryRow(
        uid: String = string(),
        syncStatusCode: Int = SyncStatusConstants.SYNCED_CODE,
        updateTimestamp: Long = long(),
        name: String = string(),
        iconId: String = string()
    ) = CategoryRow(
        uid = uid,
        syncStatusCode = syncStatusCode,
        updateTimestamp = updateTimestamp,
        name = name,
        iconId = iconId
    )

    fun transactionWithCategory(
        transactionRow: TransactionRow,
        categoryRow: CategoryRow
    ) = TransactionWithCategory(
        transaction = transactionRow,
        category = categoryRow
    )

    fun walletRow(
        uid: String = string(),
        name: String = string(),
        syncStatusCode: Int = SyncStatusConstants.SYNCED_CODE,
        updateTimestamp: Long = long()
    ) = WalletRow(
        uid = uid,
        name = name,
        syncStatusCode = syncStatusCode,
        updateTimestamp = updateTimestamp
    )
}
