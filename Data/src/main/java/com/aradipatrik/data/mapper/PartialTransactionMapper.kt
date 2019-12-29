package com.aradipatrik.data.mapper

import com.aradipatrik.data.model.TransactionPartialEntity
import com.aradipatrik.domain.model.Transaction
import javax.inject.Inject

class PartialTransactionMapper @Inject constructor() {
    fun mapToEntity(domain: Transaction) = TransactionPartialEntity(
        id = domain.id,
        categoryId = domain.category.id,
        amount = domain.amount,
        memo = domain.memo,
        date = domain.date,
        updatedTimeStamp = TimestampProvider.now(),
        syncStatus = SyncStatus.None
    )
}
