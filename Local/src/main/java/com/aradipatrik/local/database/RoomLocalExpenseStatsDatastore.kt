package com.aradipatrik.local.database

import com.aradipatrik.data.datastore.stats.LocalExpenseStatsDatastore
import com.aradipatrik.local.database.model.stats.ExpenseStatsDao
import org.joda.time.YearMonth

class RoomLocalExpenseStatsDatastore(
    private val expenseStatsDao: ExpenseStatsDao
) : LocalExpenseStatsDatastore {
    override fun getAllTimeIncome(walletId: String) = expenseStatsDao.getIncomeInWallet(walletId)

    override fun getAllTimeExpense(walletId: String) = expenseStatsDao.getExpenseInWallet(walletId)

    override fun getMonthlyIncome(yearMonth: YearMonth, walletId: String) =
        yearMonth.toInterval().let { asInterval ->
            expenseStatsDao.getIncomeInWalletInInterval(
                asInterval.startMillis,
                asInterval.endMillis,
                walletId
            )
        }

    override fun getMonthlyExpense(yearMonth: YearMonth, walletId: String) =
        yearMonth.toInterval().let { asInterval ->
            expenseStatsDao.getExpenseInWalletInInterval(
                asInterval.startMillis,
                asInterval.endMillis,
                walletId
            )
        }
}
