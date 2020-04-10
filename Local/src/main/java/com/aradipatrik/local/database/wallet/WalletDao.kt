package com.aradipatrik.local.database.wallet

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.aradipatrik.local.database.common.CommonConstants.SYNC_STATUS_COLUMN_NAME
import com.aradipatrik.local.database.common.CommonConstants.UPDATE_TIMESTAMP_COLUMN_NAME
import com.aradipatrik.local.database.common.SyncStatusConstants
import com.aradipatrik.local.database.common.SyncStatusConstants.SYNCED_CODE
import com.aradipatrik.local.database.common.WalletConstants
import com.aradipatrik.local.database.common.WalletConstants.TABLE_NAME
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single

@Dao
interface WalletDao {
    companion object Queries {
        const val GET_ALL_WALLETS = "SELECT * FROM $TABLE_NAME"
        const val GET_PENDING_WALLETS = "SELECT * FROM $TABLE_NAME " +
            "WHERE $SYNC_STATUS_COLUMN_NAME != $SYNCED_CODE"
        const val CLEAR_PENDING = "DELETE FROM $TABLE_NAME " +
            "WHERE $SYNC_STATUS_COLUMN_NAME != $SYNCED_CODE"
        const val GET_LAST_SYNC_TIME = "SELECT MAX($UPDATE_TIMESTAMP_COLUMN_NAME) " +
            "FROM $TABLE_NAME"
        const val SET_DELETED = "UPDATE $TABLE_NAME " +
            "SET $SYNC_STATUS_COLUMN_NAME = ${SyncStatusConstants.TO_DELETE_CODE} " +
            "WHERE ${WalletConstants.ID_COLUMN_NAME} = :id"
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
