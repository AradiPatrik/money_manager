package com.aradipatrik.testing

import com.aradipatrik.data.mapper.SyncStatus
import com.aradipatrik.data.model.CategoryEntity
import com.aradipatrik.data.model.TransactionJoinedEntity
import com.aradipatrik.data.model.TransactionPartialEntity
import com.aradipatrik.testing.DomainLayerMocks.date
import com.aradipatrik.testing.DomainLayerMocks.int
import com.aradipatrik.testing.DomainLayerMocks.long
import com.aradipatrik.testing.DomainLayerMocks.string
import org.joda.time.DateTime

object DataLayerMocks {
    fun categoryEntity(
        id: String = string(),
        name: String = string(),
        iconId: String = string(),
        lastUpdateTimestamp: Long = long(),
        syncStatus: SyncStatus = SyncStatus.None
    ) = CategoryEntity(id, name, iconId, lastUpdateTimestamp, syncStatus)

    fun joinedTransactionEntity(
        id: String = string(),
        category: CategoryEntity = categoryEntity(),
        amount: Int = int(),
        memo: String = string(),
        date: DateTime = date(),
        lastUpdateTimestamp: Long = long(),
        syncStatus: SyncStatus = SyncStatus.None
    ) = TransactionJoinedEntity(id, category, amount, memo, date, lastUpdateTimestamp, syncStatus)

    fun partialTransactionEntity(
        id: String = string(),
        categoryId: String = string(),
        amount: Int = int(),
        memo: String = string(),
        date: DateTime = date(),
        lastUpdateTimestamp: Long = long(),
        syncStatus: SyncStatus = SyncStatus.None
    ) = TransactionPartialEntity(id, categoryId, amount, memo, date, lastUpdateTimestamp, syncStatus)
}
