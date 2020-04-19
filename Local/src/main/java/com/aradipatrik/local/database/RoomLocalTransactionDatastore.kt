package com.aradipatrik.local.database

import com.aradipatrik.data.datastore.transaction.LocalTransactionDatastore
import com.aradipatrik.data.mapper.SyncStatus
import com.aradipatrik.data.model.TransactionWithCategoryDataModel
import com.aradipatrik.data.model.TransactionWithIdsDataModel
import com.aradipatrik.local.database.mapper.TransactionRowMapper
import com.aradipatrik.local.database.model.transaction.TransactionDao
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import org.joda.time.Interval

class RoomLocalTransactionDatastore(
    private val transactionDao: TransactionDao,
    private val transactionMapper: TransactionRowMapper
) : LocalTransactionDatastore {
    override fun getInInterval(interval: Interval, walletId: String): Observable<List<TransactionWithCategoryDataModel>> =
        transactionDao.getInInterval(interval.startMillis, interval.endMillis, walletId)
            .map { rows ->
                rows.map(transactionMapper::mapToJoinedEntity)
            }

    override fun updateWith(elements: List<TransactionWithIdsDataModel>): Completable =
        transactionDao.insert(elements.map(transactionMapper::mapToRow))

    override fun getPending(): Single<List<TransactionWithIdsDataModel>> =
        transactionDao.getPendingTransactions()
            .map { rows ->
                rows.map(transactionMapper::mapToPartialEntity)
            }

    override fun clearPending(): Completable = transactionDao.clearPending()

    override fun getLastSyncTime(): Single<Long> = transactionDao.getLastSyncTime()
        .switchIfEmpty(Maybe.just(0L))
        .toSingle()

    override fun getAll(): Observable<List<TransactionWithIdsDataModel>> =
        transactionDao.getAllTransactions()
            .map { rows ->
                rows.map(transactionMapper::mapToPartialEntity)
            }

    override fun add(item: TransactionWithIdsDataModel): Completable =
        transactionDao.insert(
            listOf(transactionMapper.mapToRow(item.copy(syncStatus = SyncStatus.ToAdd)))
        )

    override fun update(item: TransactionWithIdsDataModel): Completable =
        transactionDao.insert(
            listOf(transactionMapper.mapToRow(item.copy(syncStatus = SyncStatus.ToUpdate)))
        )

    override fun delete(id: String): Completable = transactionDao.setDeleted(id)
}
