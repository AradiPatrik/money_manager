package com.aradipatrik.data.test

import com.aradipatrik.data.model.CategoryEntity
import com.aradipatrik.data.model.TransactionEntity
import com.aradipatrik.domain.test.MockDataFactory.boolean
import com.aradipatrik.domain.test.MockDataFactory.date
import com.aradipatrik.domain.test.MockDataFactory.int
import com.aradipatrik.domain.test.MockDataFactory.long
import com.aradipatrik.domain.test.MockDataFactory.string
import org.joda.time.DateTime

object MockDataFactory {
    fun categoryEntity(
        id: String = string(),
        name: String = string(),
        iconId: String = string(),
        lastUpdateTimestamp: Long = long(),
        isDeleted: Boolean = boolean()
    ) = CategoryEntity(id, name, iconId, lastUpdateTimestamp, isDeleted)

    fun transactionEntity(
        id: String = string(),
        category: CategoryEntity = categoryEntity(),
        amount: Int = int(),
        memo: String = string(),
        date: DateTime = date(),
        lastUpdateTimestamp: Long = long(),
        isDeleted: Boolean = boolean()
    ) = TransactionEntity(id, category, amount, memo, date, lastUpdateTimestamp, isDeleted)
}
