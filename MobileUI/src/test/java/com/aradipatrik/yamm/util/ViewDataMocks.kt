package com.aradipatrik.yamm.util

import com.aradipatrik.presentation.presentations.CategoryPresentation
import com.aradipatrik.presentation.presentations.TransactionPresentation
import com.aradipatrik.testing.DomainLayerMocks.boolean
import com.aradipatrik.testing.DomainLayerMocks.int
import com.aradipatrik.testing.DomainLayerMocks.string
import com.aradipatrik.yamm.features.add.transaction.model.CategoryItemViewData
import com.aradipatrik.yamm.features.history.model.TransactionItemViewData
import com.aradipatrik.yamm.util.PresentationLayerMocks.categoryPresentation
import com.aradipatrik.yamm.util.PresentationLayerMocks.transactionPresentation

object ViewDataMocks {
    fun transactionItemViewData(
        memo: String = string(),
        categoryIconResId: Int = int(),
        amount: Int = int(),
        colorResId: Int = int(),
        categoryName: String = string(),
        presentationRef: TransactionPresentation = transactionPresentation()
    ) = TransactionItemViewData(
        memo = memo,
        categoryIconResId = categoryIconResId,
        amount = amount,
        colorResId = colorResId,
        categoryName = categoryName,
        presentationRef = presentationRef
    )

    fun categoryViewData(
        presentationRef: CategoryPresentation = categoryPresentation(),
        iconResId: Int = int(),
        colorResId: Int = int(),
        name: String = string(),
        isSelected: Boolean = boolean()
    ) = CategoryItemViewData(
        presentationRef = presentationRef,
        categoryName = name,
        colorResId = colorResId,
        iconResId = iconResId,
        isSelected = isSelected
    )
}