package com.aradipatrik.local.database

import com.aradipatrik.data.datastore.wallet.LocalWalletDatastore
import com.aradipatrik.data.mapper.SyncStatus
import com.aradipatrik.data.mapper.WalletMapper
import com.aradipatrik.data.model.WalletEntity
import com.aradipatrik.local.database.mapper.WalletRowMapper
import com.aradipatrik.local.database.wallet.WalletDao
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single

class RoomLocalWalletDatastore(
    private val walletDao: WalletDao,
    private val walletRowMapper: WalletRowMapper
): LocalWalletDatastore {

    override fun updateWith(elements: List<WalletEntity>): Completable =
        walletDao.insert(elements.map(walletRowMapper::mapToRow))

    override fun getPending(): Single<List<WalletEntity>> =
        walletDao.getPendingWallets().map { rows ->
            rows.map(walletRowMapper::mapToEntity)
        }

    override fun clearPending(): Completable =
        walletDao.clearPending()

    override fun getLastSyncTime(): Single<Long> =
        walletDao.getLastSyncTime()
            .switchIfEmpty(Maybe.just(0L))
            .toSingle()

    override fun getAll(): Observable<List<WalletEntity>> =
        walletDao.getAllWallets().map { rows ->
            rows.map(walletRowMapper::mapToEntity)
        }

    override fun add(item: WalletEntity): Completable =
        walletDao.insert(
            listOf(walletRowMapper.mapToRow(item.copy(syncStatus = SyncStatus.ToAdd)))
        )

    override fun update(item: WalletEntity): Completable =
        walletDao.insert(
            listOf(walletRowMapper.mapToRow(item.copy(syncStatus = SyncStatus.ToUpdate)))
        )

    override fun delete(id: String): Completable =
        walletDao.setDeleted(id)

}