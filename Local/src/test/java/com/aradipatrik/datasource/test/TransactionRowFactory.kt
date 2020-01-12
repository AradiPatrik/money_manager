package com.aradipatrik.datasource.test

import com.aradipatrik.local.database.category.CategoryRow
import com.aradipatrik.local.database.common.SyncStatusConstants
import com.aradipatrik.local.database.transaction.TransactionRow
import com.aradipatrik.local.database.transaction.TransactionWithCategory
import com.aradipatrik.testing.DomainLayerMocks.int
import com.aradipatrik.testing.DomainLayerMocks.long
import com.aradipatrik.testing.DomainLayerMocks.string

object TransactionRowFactory {
    fun transactionRow(
        uid: String = string(),
        updateTimestamp: Long = long(),
        memo: String = string(),
        amount: Int = int(),
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
}