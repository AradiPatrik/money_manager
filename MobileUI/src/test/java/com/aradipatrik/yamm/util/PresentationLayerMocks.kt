package com.aradipatrik.yamm.util

import com.aradipatrik.presentation.presentations.CategoryPresentation
import com.aradipatrik.presentation.presentations.TransactionPresentation
import com.aradipatrik.testing.DomainLayerMocks.date
import com.aradipatrik.testing.DomainLayerMocks.int
import com.aradipatrik.testing.DomainLayerMocks.string
import org.joda.time.DateTime

object PresentationLayerMocks {

    fun categoryPresentation(
        id: String = string(),
        iconId: String = string(),
        categoryName: String = string()
    ) = CategoryPresentation(id, categoryName, iconId)

    fun transactionPresentation(
        id: String = string(),
        amount: Int = int(),
        categoryPresentation: CategoryPresentation = categoryPresentation(),
        date: DateTime = date(),
        memo: String = string()
    ) = TransactionPresentation(id, amount, categoryPresentation, date, memo)
}