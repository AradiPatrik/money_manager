package com.aradipatrik.yamm.features.history.model

import com.aradipatrik.presentation.presentations.TransactionPresentation
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormatter


sealed class TransactionViewData

data class TransactionItemViewData(
    val memo: String,
    val categoryIconResId: Int,
    val amount: Int,
    val colorResId: Int,
    val categoryName: String,
    val presentationRef: TransactionPresentation
): TransactionViewData()

data class TransactionHeaderViewData(val localDateRef: LocalDate): TransactionViewData() {
    val asFormattedString: String = localDateRef.toString("YYYY-MM-DD")
}
