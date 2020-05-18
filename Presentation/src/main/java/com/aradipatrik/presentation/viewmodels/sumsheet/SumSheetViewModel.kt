package com.aradipatrik.presentation.viewmodels.sumsheet

import com.airbnb.mvrx.Async
import com.aradipatrik.domain.model.Transaction
import org.joda.time.DateTime

data class SplashState(
    val selectedMonthOperation: Async<DateTime>,
    val transactionsInSelectedMonthOperation: Async<List<Transaction>>,
    val incomeThisMonth: Int?,
    val expenseThisMonth: Int?,
    val grandTotal: Int?,
    val monthlyTotal: Int?
)

class SumSheetViewModel {
    
}