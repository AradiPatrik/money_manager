package com.aradipatrik.local.database

import com.aradipatrik.data.datasource.transaction.LocalTransactionDataStore
import com.aradipatrik.data.mapper.SyncStatus
import com.aradipatrik.data.model.TransactionJoinedEntity
import com.aradipatrik.data.model.TransactionPartialEntity
import com.aradipatrik.local.database.common.SyncStatusConstants.TO_ADD_CODE
import com.aradipatrik.local.database.common.SyncStatusConstants.TO_UPDATE_CODE
import com.aradipatrik.local.database.mapper.TransactionRowMapper
import com.aradipatrik.local.database.transaction.TransactionDao
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import org.joda.time.Interval
import javax.inject.Inject

class RoomLocalTransactionDataSource @Inject constructor(
    private val transactionDao: TransactionDao,
    private val transactionMapper: TransactionRowMapper
) : LocalTransactionDataStore {
    override fun getInInterval(interval: Interval): Observable<List<TransactionJoinedEntity>> =
        transactionDao.getInInterval(interval.startMillis, interval.endMillis)
            .map { rows ->
                rows.map(transactionMapper::mapToJoinedEntity)
            }

    override fun updateWith(elements: List<TransactionPartialEntity>): Completable =
        transactionDao.insert(elements.map(transactionMapper::mapToRow))

    override fun getPending(): Single<List<TransactionPartialEntity>> =
        transactionDao.getPendingTransactions()
            .map { rows ->
                rows.map(transactionMapper::mapToPartialEntity)
            }

    override fun clearPending(): Completable = transactionDao.clearPending()

    override fun getLastSyncTime(): Single<Long> = transactionDao.getLastSyncTime()
        .switchIfEmpty(Maybe.just(0L))
        .toSingle()

    override fun getAll(): Observable<List<TransactionPartialEntity>> =
        transactionDao.getAllTransactions()
            .map { rows ->
                rows.map(transactionMapper::mapToPartialEntity)
            }

    override fun add(item: TransactionPartialEntity): Completable =
        transactionDao.insert(
            listOf(transactionMapper.mapToRow(item.copy(syncStatus = SyncStatus.ToAdd)))
        )

    override fun update(item: TransactionPartialEntity): Completable =
        transactionDao.insert(
            listOf(transactionMapper.mapToRow(item.copy(syncStatus = SyncStatus.ToUpdate)))
        )

    override fun delete(id: String): Completable = transactionDao.setDeleted(id)
}