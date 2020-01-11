package com.aradipatrik.local.database.category

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.aradipatrik.local.database.common.CategoryConstants.ID_COLUMN_NAME
import com.aradipatrik.local.database.common.CategoryConstants.SYNC_STATUS_COLUMN_NAME
import com.aradipatrik.local.database.common.CategoryConstants.TABLE_NAME
import com.aradipatrik.local.database.common.CategoryConstants.UPDATE_TIMESTAMP_COLUMN_NAME
import com.aradipatrik.local.database.common.SyncStatusConstants.SYNCED_CODE
import com.aradipatrik.local.database.common.SyncStatusConstants.TO_DELETE_CODE
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

@Dao
interface CategoryDao {
    companion object Queries{
        const val GET_ALL_CATEGORIES = "SELECT * FROM $TABLE_NAME"
        const val GET_PENDING_CATEGORIES = "SELECT * FROM $TABLE_NAME WHERE $SYNC_STATUS_COLUMN_NAME != $SYNCED_CODE"
        const val CLEAR_PENDING = "DELETE FROM $TABLE_NAME where $SYNC_STATUS_COLUMN_NAME != $SYNCED_CODE"
        const val GET_LAST_SYNC_TIME = "SELECT MAX($UPDATE_TIMESTAMP_COLUMN_NAME) FROM $TABLE_NAME"
        const val SET_DELETED = "UPDATE $TABLE_NAME SET $SYNC_STATUS_COLUMN_NAME = $TO_DELETE_CODE WHERE $ID_COLUMN_NAME = :id"
    }

    @Query(GET_ALL_CATEGORIES)
    fun getAllCategories(): Observable<List<CategoryRow>>

    @Query(GET_PENDING_CATEGORIES)
    fun getPendingCategories(): Single<List<CategoryRow>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(rows: List<CategoryRow>): Completable

    @Query(CLEAR_PENDING)
    fun clearPending(): Completable

    @Query(GET_LAST_SYNC_TIME)
    fun getLastSyncTime(): Single<Long>

    @Query(SET_DELETED)
    fun setDeleted(id: String): Completable
}