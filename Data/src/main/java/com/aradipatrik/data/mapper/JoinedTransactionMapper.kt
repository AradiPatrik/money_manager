package com.aradipatrik.data.mapper

import com.aradipatrik.data.model.TransactionWithCategoryDataModel
import com.aradipatrik.domain.model.Transaction

class JoinedTransactionMapper(private val categoryMapper: CategoryMapper) {
    fun mapFromEntity(entity: TransactionWithCategoryDataModel) = Transaction(
        id = entity.id,
        category = categoryMapper.mapFromEntity(entity.category),
        memo = entity.memo,
        amount = entity.amount,
        date = entity.date,
        walletId = entity.walletId
    )

    fun mapToEntity(domain: Transaction) = TransactionWithCategoryDataModel(
        id = domain.id,
        date = domain.date,
        walletId = domain.walletId,
        amount = domain.amount,
        memo = domain.memo,
        category = categoryMapper.mapToEntity(domain.category),
        syncStatus = SyncStatus.None,
        updatedTimeStamp = TimestampProvider.now()
    )
}
