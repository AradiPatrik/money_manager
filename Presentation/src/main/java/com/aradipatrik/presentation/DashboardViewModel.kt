package com.aradipatrik.presentation

import com.airbnb.mvrx.MvRxState

data class DashboardState(
    val transactionsThisMonth: List<TransactionPresentation>
): MvRxState