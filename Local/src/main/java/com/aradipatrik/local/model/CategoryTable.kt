package com.aradipatrik.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = CategoryConstants.TABLE_NAME)
data class CategoryRow(
    @PrimaryKey @ColumnInfo(name = CategoryConstants.ID_COLUMN_NAME) val uid: String,
    @ColumnInfo(name = CategoryConstants.NAME_COLUMN_NAME) val name: String,
    @ColumnInfo(name = CategoryConstants.ICON_ID_COLUMN_NAME) val iconId: String,
    @ColumnInfo(name = CategoryConstants.UPDATE_TIMESTAMP_COLUMN_NAME) val updateTimestamp: String
)