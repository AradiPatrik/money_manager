package com.aradipatrik.local.database.wallet

import androidx.room.*
import com.aradipatrik.local.database.common.SyncStatusConstants
import com.aradipatrik.local.database.common.WalletConstants
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single

@Dao
interface WalletDao {
    companion object Queries {
        const val GET_ALL_WALLETS = "SELECT * FROM ${WalletConstants.TABLE_NAME}"
        const val GET_PENDING_WALLETS =
            "SELECT * FROM ${WalletConstants.TABLE_NAME} WHERE ${WalletConstants.SYNC_STATUS_COLUMN_NAME} != ${SyncStatusConstants.SYNCED_CODE}"
        const val CLEAR_PENDING =
            "DELETE FROM ${WalletConstants.TABLE_NAME} where ${WalletConstants.SYNC_STATUS_COLUMN_NAME} != ${SyncStatusConstants.SYNCED_CODE}"
        const val GET_LAST_SYNC_TIME =
            "SELECT MAX(${WalletConstants.UPDATE_TIMESTAMP_COLUMN_NAME}) FROM ${WalletConstants.TABLE_NAME}"
        const val SET_DELETED =
            "UPDATE ${WalletConstants.TABLE_NAME} SET ${WalletConstants.SYNC_STATUS_COLUMN_NAME} = ${SyncStatusConstants.TO_DELETE_CODE} WHERE ${WalletConstants.ID_COLUMN_NAME} = :id"
    }

    @Query(GET_ALL_WALLETS)
    fun getAllWallets(): Observable<List<WalletRow>>

    @Query(GET_PENDING_WALLETS)
    fun getPendingWallets(): Single<List<WalletRow>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(rows: List<WalletRow>): Completable

    @Query(CLEAR_PENDING)
    fun clearPending(): Completable

    @Query(GET_LAST_SYNC_TIME)
    fun getLastSyncTime(): Maybe<Long>

    @Query(SET_DELETED)
    fun setDeleted(id: String): Completable
}