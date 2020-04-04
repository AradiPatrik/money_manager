package com.aradipatrik.local.database.mapper

import com.aradipatrik.data.mapper.SyncStatus
import com.aradipatrik.data.model.TransactionJoinedEntity
import com.aradipatrik.data.model.TransactionPartialEntity
import com.aradipatrik.local.database.transaction.TransactionRow
import com.aradipatrik.local.database.transaction.TransactionWithCategory
import org.joda.time.DateTime

class TransactionRowMapper(
    private val categoryRowMapper: CategoryRowMapper
) {
    fun mapToRow(e: TransactionPartialEntity) = TransactionRow(
        uid = e.id,
        categoryId = e.categoryId,
        amount = e.amount,
        date = e.date.millis,
        memo = e.memo,
        updateTimestamp = e.updatedTimeStamp,
        syncStatusCode = e.syncStatus.code
    )

    fun mapToPartialEntity(r: TransactionRow) = TransactionPartialEntity(
        id = r.uid,
        categoryId = r.categoryId,
        amount = r.amount,
        date = DateTime(r.date),
        memo = r.memo,
        updatedTimeStamp = r.updateTimestamp,
        syncStatus = SyncStatus.fromCode(r.syncStatusCode)
    )

    fun mapToJoinedEntity(r: TransactionWithCategory) = TransactionJoinedEntity(
        id = r.transaction.uid,
        amount = r.transaction.amount,
        date = DateTime(r.transaction.date),
        updatedTimeStamp = r.transaction.updateTimestamp,
        syncStatus = SyncStatus.fromCode(r.transaction.syncStatusCode),
        memo = r.transaction.memo,
        category = categoryRowMapper.mapToEntity(r.category)
    )
}
