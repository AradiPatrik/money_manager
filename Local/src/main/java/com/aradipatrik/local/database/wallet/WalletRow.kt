package com.aradipatrik.local.database.wallet

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.aradipatrik.local.database.common.WalletConstants

@Entity(tableName = WalletConstants.TABLE_NAME)
data class WalletRow(
    @PrimaryKey @ColumnInfo(name = WalletConstants.ID_COLUMN_NAME) val uid: String,
    @ColumnInfo(name = WalletConstants.NAME_COLUMN_NAME) val name: String,
    @ColumnInfo(name = WalletConstants.SYNC_STATUS_COLUMN_NAME) val syncStatusCode: Int,
    @ColumnInfo(name = WalletConstants.UPDATE_TIMESTAMP_COLUMN_NAME) val updateTimestamp: Long
)
