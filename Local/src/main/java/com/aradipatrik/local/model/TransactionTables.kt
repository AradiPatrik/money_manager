package com.aradipatrik.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.aradipatrik.local.model.TransactionConstants

@Entity(tableName = TransactionConstants.TABLE_NAME)
data class TransactionRow(
    @PrimaryKey @ColumnInfo(name = TransactionConstants.ID_COLUMN_NAME) val uid: String,
    @ColumnInfo(name = TransactionConstants.AMOUNT_COLUMN_NAME) val amount: Int,
    @ColumnInfo(name = TransactionConstants.MEMO_COLUMN_NAME) val memo: String,
    @ColumnInfo(name = TransactionConstants.DATE_COLUMN_NAME) val date: Long,
    @ColumnInfo(name = TransactionConstants.UPDATE_TIMESTAMP_COLUMN_NAME) val updateTimestamp: Long
)

@Entity(tableName = PendingTransactionConstants.TABLE_NAME)
data class PendingTransactionRow(
    @PrimaryKey @ColumnInfo(name = PendingTransactionConstants.ID_COLUMN_NAME) val uid: String,
    @ColumnInfo(name = PendingTransactionConstants.AMOUNT_COLUMN_NAME) val amount: String,
    @ColumnInfo(name = PendingTransactionConstants.MEMO_COLUMN_NAME) val memo: String,
    @ColumnInfo(name = PendingTransactionConstants.DATE_COLUMN_NAME) val date: Long,
    @ColumnInfo(name = PendingTransactionConstants.UPDATE_TIMESTAMP_COLUMN_NAME) val updateTimestamp: Long
)
