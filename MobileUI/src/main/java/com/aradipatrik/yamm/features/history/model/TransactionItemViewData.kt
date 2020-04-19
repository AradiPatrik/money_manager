package com.aradipatrik.yamm.features.history.model

import com.aradipatrik.presentation.presentations.TransactionPresentationModel
import org.joda.time.LocalDate

sealed class TransactionViewData

data class TransactionItemViewData(
    val memo: String,
    val categoryIconResId: Int,
    val amount: Int,
    val colorResId: Int,
    val categoryName: String,
    val presentationModelRef: TransactionPresentationModel
) : TransactionViewData()

data class TransactionHeaderViewData(val localDateRef: LocalDate) : TransactionViewData() {
    val asFormattedString: String = localDateRef.toString("YYYY-MM-dd")
}
