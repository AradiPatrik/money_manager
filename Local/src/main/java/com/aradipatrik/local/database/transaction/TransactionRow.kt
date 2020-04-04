package com.aradipatrik.local.database.transaction

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.aradipatrik.local.database.common.CommonConstants
import com.aradipatrik.local.database.common.TransactionConstants

@Entity(tableName = TransactionConstants.TABLE_NAME)
data class TransactionRow(
    @PrimaryKey @ColumnInfo(name = TransactionConstants.ID_COLUMN_NAME) val uid: String,
    @ColumnInfo(name = TransactionConstants.CATEGORY_ID_COLUMN_NAME) val categoryId: String,
    @ColumnInfo(name = TransactionConstants.AMOUNT_COLUMN_NAME) val amount: Int,
    @ColumnInfo(name = TransactionConstants.MEMO_COLUMN_NAME) val memo: String,
    @ColumnInfo(name = TransactionConstants.DATE_COLUMN_NAME) val date: Long,
    @ColumnInfo(name = CommonConstants.SYNC_STATUS_COLUMN_NAME) val syncStatusCode: Int,
    @ColumnInfo(name = CommonConstants.UPDATE_TIMESTAMP_COLUMN_NAME) val updateTimestamp: Long
)
