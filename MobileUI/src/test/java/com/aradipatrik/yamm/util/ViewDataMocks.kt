package com.aradipatrik.yamm.util

import com.aradipatrik.presentation.presentations.CategoryPresentationModel
import com.aradipatrik.presentation.presentations.TransactionPresentationModel
import com.aradipatrik.testing.CommonMocks.boolean
import com.aradipatrik.testing.CommonMocks.int
import com.aradipatrik.testing.CommonMocks.string
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
        presentationModelRef: TransactionPresentationModel = transactionPresentation()
    ) = TransactionItemViewData(
        memo = memo,
        categoryIconResId = categoryIconResId,
        amount = amount,
        colorResId = colorResId,
        categoryName = categoryName,
        presentationModelRef = presentationModelRef
    )

    fun categoryViewData(
        presentationModelRef: CategoryPresentationModel = categoryPresentation(),
        iconResId: Int = int(),
        colorResId: Int = int(),
        name: String = string(),
        isSelected: Boolean = boolean()
    ) = CategoryItemViewData(
        presentationModelRef = presentationModelRef,
        categoryName = name,
        colorResId = colorResId,
        iconResId = iconResId,
        isSelected = isSelected
    )
}
