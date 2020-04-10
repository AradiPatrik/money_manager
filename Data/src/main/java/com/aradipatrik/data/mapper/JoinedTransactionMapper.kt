package com.aradipatrik.data.mapper

import com.aradipatrik.data.model.TransactionWithCategory
import com.aradipatrik.domain.model.Transaction

class JoinedTransactionMapper(private val categoryMapper: CategoryMapper) {
    fun mapFromEntity(entity: TransactionWithCategory) = Transaction(
        id = entity.id,
        category = categoryMapper.mapFromEntity(entity.category),
        memo = entity.memo,
        amount = entity.amount,
        date = entity.date
    )

    fun mapToEntity(domain: Transaction) = TransactionWithCategory(
        id = domain.id,
        date = domain.date,
        walletId = "",
        amount = domain.amount,
        memo = domain.memo,
        category = categoryMapper.mapToEntity(domain.category),
        syncStatus = SyncStatus.None,
        updatedTimeStamp = TimestampProvider.now()
    )
}
