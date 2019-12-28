package com.aradipatrik.data.test.common

import com.aradipatrik.data.mapper.SyncStatus
import com.aradipatrik.data.model.CategoryEntity
import com.aradipatrik.data.model.TransactionEntity
import com.aradipatrik.testing.MockDomainDataFactory.date
import com.aradipatrik.testing.MockDomainDataFactory.int
import com.aradipatrik.testing.MockDomainDataFactory.long
import com.aradipatrik.testing.MockDomainDataFactory.string
import org.joda.time.DateTime

object MockDataFactory {
    fun categoryEntity(
        id: String = string(),
        name: String = string(),
        iconId: String = string(),
        lastUpdateTimestamp: Long = long(),
        syncStatus: SyncStatus = SyncStatus.None
    ) = CategoryEntity(id, name, iconId, lastUpdateTimestamp, syncStatus)

    fun transactionEntity(
        id: String = string(),
        category: CategoryEntity = categoryEntity(),
        amount: Int = int(),
        memo: String = string(),
        date: DateTime = date(),
        lastUpdateTimestamp: Long = long(),
        syncStatus: SyncStatus = SyncStatus.None
    ) = TransactionEntity(id, category, amount, memo, date, lastUpdateTimestamp, syncStatus)
}
