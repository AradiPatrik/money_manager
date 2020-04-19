package com.aradipatrik.local.database

import com.aradipatrik.data.datastore.wallet.LocalWalletDatastore
import com.aradipatrik.data.mapper.SyncStatus
import com.aradipatrik.data.model.WalletDataModel
import com.aradipatrik.local.database.mapper.WalletRowMapper
import com.aradipatrik.local.database.model.wallet.WalletDao
import com.f2prateek.rx.preferences2.RxSharedPreferences
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single

class RoomLocalWalletDatastore(
    private val walletDao: WalletDao,
    private val walletRowMapper: WalletRowMapper,
    private val rxPreferences: RxSharedPreferences
) : LocalWalletDatastore {
    companion object {
        const val SELECTED_WALLET_KEY = "SELECTED_WALLET_KEY"
    }

    override fun updateWith(elements: List<WalletDataModel>): Completable =
        walletDao.insert(elements.map(walletRowMapper::mapToRow))

    override fun getPending(): Single<List<WalletDataModel>> =
        walletDao.getPendingWallets().map { rows ->
            rows.map(walletRowMapper::mapToEntity)
        }

    override fun clearPending(): Completable =
        walletDao.clearPending()

    override fun getLastSyncTime(): Single<Long> =
        walletDao.getLastSyncTime()
            .switchIfEmpty(Maybe.just(0L))
            .toSingle()

    override fun getAll(): Observable<List<WalletDataModel>> =
        walletDao.getAllWallets().map { rows ->
            rows.map(walletRowMapper::mapToEntity)
        }

    override fun add(item: WalletDataModel): Completable =
        walletDao.insert(
            listOf(walletRowMapper.mapToRow(item.copy(syncStatus = SyncStatus.ToAdd)))
        )

    override fun update(item: WalletDataModel): Completable =
        walletDao.insert(
            listOf(walletRowMapper.mapToRow(item.copy(syncStatus = SyncStatus.ToUpdate)))
        )

    override fun delete(id: String): Completable =
        walletDao.setDeleted(id)

    override fun setSelected(walletId: String) = Completable.fromAction {
        rxPreferences.getString(SELECTED_WALLET_KEY).set(walletId)
    }

    override fun getSelected() =
        rxPreferences.getString(SELECTED_WALLET_KEY)
            .asObservable()
            .flatMap { selectedWalletId ->
                walletDao.getWalletById(selectedWalletId)
            }
            .map(walletRowMapper::mapToEntity)
            .firstOrError()
}