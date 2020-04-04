package com.aradipatrik.presentation.datahelpers

import com.aradipatrik.presentation.presentations.CategoryPresentation
import com.aradipatrik.presentation.presentations.TransactionPresentation
import com.aradipatrik.testing.DomainLayerMocks.date
import com.aradipatrik.testing.DomainLayerMocks.int
import com.aradipatrik.testing.DomainLayerMocks.string
import org.joda.time.DateTime

object MockDataFactory {
    fun categoryPresentation(
        id: String = string(),
        name: String = string(),
        iconId: String = string()
    ) = CategoryPresentation(
        id = id,
        name = name,
        iconId = iconId
    )

    fun transactionPresentation(
        id: String = string(),
        amount: Int = int(),
        category: CategoryPresentation = categoryPresentation(),
        date: DateTime = date(),
        memo: String = string()
    ) = TransactionPresentation(
        id = id,
        amount = amount,
        category = category,
        date = date,
        memo = memo
    )
}