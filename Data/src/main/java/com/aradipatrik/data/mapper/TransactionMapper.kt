package com.aradipatrik.data.mapper

import com.aradipatrik.data.model.TransactionEntity
import com.aradipatrik.domain.model.Transaction
import javax.inject.Inject

class TransactionMapper @Inject constructor(
    private val categoryMapper: CategoryMapper
) : EntityMapper<TransactionEntity, Transaction> {
    override fun mapFromEntity(entity: TransactionEntity) = Transaction(
        id = entity.id,
        category = categoryMapper.mapFromEntity(entity.category),
        amount = entity.amount,
        memo = entity.memo,
        date = entity.date
    )

    override fun mapToEntity(domain: Transaction) = TransactionEntity(
        id = domain.id,
        category = categoryMapper.mapToEntity(domain.category),
        amount = domain.amount,
        memo = domain.memo,
        date = domain.date,
        updatedTimeStamp = TimestampProvider.now(),
        isDeleted = false
    )
}
