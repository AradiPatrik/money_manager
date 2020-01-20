package com.aradipatrik.yamm.util

import com.aradipatrik.presentation.presentations.TransactionPresentation
import com.aradipatrik.testing.DomainLayerMocks.int
import com.aradipatrik.testing.DomainLayerMocks.string
import com.aradipatrik.yamm.features.history.model.TransactionItemViewData
import com.aradipatrik.yamm.util.PresentationLayerMocks.transactionPresentation

object ViewDataMocks {
    fun transactionViewData(
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
}