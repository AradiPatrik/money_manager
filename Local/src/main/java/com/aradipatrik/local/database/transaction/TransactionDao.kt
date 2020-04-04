package com.aradipatrik.local.database.transaction

import androidx.room.*
import com.aradipatrik.local.database.common.CommonConstants.SYNC_STATUS_COLUMN_NAME
import com.aradipatrik.local.database.common.CommonConstants.UPDATE_TIMESTAMP_COLUMN_NAME
import com.aradipatrik.local.database.common.SyncStatusConstants.SYNCED_CODE
import com.aradipatrik.local.database.common.SyncStatusConstants.TO_DELETE_CODE
import com.aradipatrik.local.database.common.TransactionConstants.DATE_COLUMN_NAME
import com.aradipatrik.local.database.common.TransactionConstants.ID_COLUMN_NAME
import com.aradipatrik.local.database.common.TransactionConstants.TABLE_NAME
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single

@Dao
interface TransactionDao {
    companion object Queries {
        const val GET_ALL_TRANSACTIONS = "SELECT * FROM $TABLE_NAME"
        const val GET_PENDING_TRANSACTIONS =
            "SELECT * FROM $TABLE_NAME WHERE $SYNC_STATUS_COLUMN_NAME != $SYNCED_CODE"
        const val GET_IN_INTERVAL =
            "SELECT * FROM $TABLE_NAME WHERE $SYNC_STATUS_COLUMN_NAME != " +
                    "$TO_DELETE_CODE AND $DATE_COLUMN_NAME BETWEEN :begin AND :end"
        const val CLEAR_PENDING =
            "DELETE FROM $TABLE_NAME where $SYNC_STATUS_COLUMN_NAME != $SYNCED_CODE"
        const val GET_LAST_SYNC_TIME = "SELECT MAX($UPDATE_TIMESTAMP_COLUMN_NAME) FROM $TABLE_NAME"
        const val SET_DELETED =
            "UPDATE $TABLE_NAME SET $SYNC_STATUS_COLUMN_NAME = $TO_DELETE_CODE WHERE $ID_COLUMN_NAME = :id"
    }

    @Query(GET_ALL_TRANSACTIONS)
    fun getAllTransactions(): Observable<List<TransactionRow>>

    @Query(GET_PENDING_TRANSACTIONS)
    fun getPendingTransactions(): Single<List<TransactionRow>>

    @Transaction
    @Query(GET_IN_INTERVAL)
    fun getInInterval(begin: Long, end: Long): Observable<List<TransactionWithCategory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(rows: List<TransactionRow>): Completable

    @Query(CLEAR_PENDING)
    fun clearPending(): Completable

    @Query(GET_LAST_SYNC_TIME)
    fun getLastSyncTime(): Maybe<Long>

    @Query(SET_DELETED)
    fun setDeleted(id: String): Completable
}
