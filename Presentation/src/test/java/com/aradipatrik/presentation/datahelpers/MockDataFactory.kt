package com.aradipatrik.presentation.datahelpers

import com.aradipatrik.presentation.presentations.CategoryPresentationModel
import com.aradipatrik.presentation.presentations.TransactionPresentationModel
import com.aradipatrik.testing.CommonMocks.date
import com.aradipatrik.testing.CommonMocks.int
import com.aradipatrik.testing.CommonMocks.string
import org.joda.time.DateTime

object MockDataFactory {
    fun categoryPresentation(
        id: String = string(),
        name: String = string(),
        iconId: String = string()
    ) = CategoryPresentationModel(
        id = id,
        name = name,
        iconId = iconId
    )

    fun transactionPresentation(
        id: String = string(),
        amount: Int = int(),
        categoryModel: CategoryPresentationModel = categoryPresentation(),
        date: DateTime = date(),
        memo: String = string()
    ) = TransactionPresentationModel(
        id = id,
        amount = amount,
        categoryModel = categoryModel,
        date = date,
        memo = memo
    )
}
