package com.aradipatrik.local.database.model.transaction

import androidx.room.Embedded
import androidx.room.Relation
import com.aradipatrik.local.database.model.category.CategoryRow
import com.aradipatrik.local.database.common.CategoryConstants
import com.aradipatrik.local.database.common.TransactionConstants

data class TransactionWithCategory(
    @Embedded val transaction: TransactionRow,
    @Relation(
        parentColumn = TransactionConstants.CATEGORY_ID_COLUMN_NAME,
        entityColumn = CategoryConstants.ID_COLUMN_NAME
    )
    val category: CategoryRow
)
