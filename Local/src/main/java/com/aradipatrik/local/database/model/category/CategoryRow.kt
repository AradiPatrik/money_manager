package com.aradipatrik.local.database.model.category

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.aradipatrik.local.database.common.CategoryConstants
import com.aradipatrik.local.database.common.CommonConstants

@Entity(tableName = CategoryConstants.TABLE_NAME)
data class CategoryRow(
    @PrimaryKey @ColumnInfo(name = CategoryConstants.ID_COLUMN_NAME) val uid: String,
    @ColumnInfo(name = CategoryConstants.NAME_COLUMN_NAME) val name: String,
    @ColumnInfo(name = CategoryConstants.ICON_ID_COLUMN_NAME) val iconId: String,
    @ColumnInfo(name = CommonConstants.WALLET_ID_COLUMN_NAME) val walletId: String,
    @ColumnInfo(name = CommonConstants.UPDATE_TIMESTAMP_COLUMN_NAME) val updateTimestamp: Long,
    @ColumnInfo(name = CommonConstants.SYNC_STATUS_COLUMN_NAME) val syncStatusCode: Int
)
