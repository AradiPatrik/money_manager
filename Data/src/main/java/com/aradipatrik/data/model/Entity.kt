package com.aradipatrik.data.model

import org.joda.time.DateTime

data class TransactionEntity(val id: String, val category: CategoryEntity,
                             val amount: Int, val memo: String, val date: DateTime,
                             val updatedTimeStamp: Long)
data class CategoryEntity(val id: String, val name: String, val iconId: String,
                          val lastUpdateTimestamp: Long)
