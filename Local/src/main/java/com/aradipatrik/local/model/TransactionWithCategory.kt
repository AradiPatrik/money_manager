package com.aradipatrik.local.model

import androidx.room.Embedded
import androidx.room.Relation

data class TransactionWithCategory(
    @Embedded val transaction: TransactionRow,
    @Relation(
        parentColumn = TransactionConstants.ID_COLUMN_NAME,
        entityColumn = CategoryConstants.ID_COLUMN_NAME
    )
    val category: CategoryRow
)
