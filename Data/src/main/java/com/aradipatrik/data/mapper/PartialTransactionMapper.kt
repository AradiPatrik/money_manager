package com.aradipatrik.data.mapper

import com.aradipatrik.data.model.TransactionWithIdsDataModel
import com.aradipatrik.domain.model.Transaction

class PartialTransactionMapper {
    fun mapToEntity(domain: Transaction) = TransactionWithIdsDataModel(
        id = domain.id,
        categoryId = domain.category.id,
        walletId = domain.walletId,
        amount = domain.amount,
        memo = domain.memo,
        date = domain.date,
        updatedTimeStamp = TimestampProvider.now(),
        syncStatus = SyncStatus.None
    )
}
