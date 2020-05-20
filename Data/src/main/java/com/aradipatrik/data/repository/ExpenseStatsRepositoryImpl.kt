package com.aradipatrik.data.repository

import com.aradipatrik.data.datastore.stats.LocalExpenseStatsDatastore
import com.aradipatrik.domain.interfaces.data.ExpenseStatsRepository
import com.aradipatrik.domain.model.ExpenseStats
import io.reactivex.rxkotlin.Observables
import org.joda.time.YearMonth

class ExpenseStatsRepositoryImpl(
    private val transactionDatastore: LocalExpenseStatsDatastore
) : ExpenseStatsRepository {
    override fun getAlltimeExpenseStats(walletId: String) = Observables.combineLatest(
        transactionDatastore.getAllTimeExpense(walletId),
        transactionDatastore.getAllTimeIncome(walletId)
    )
        .map { (expense, income) ->
            ExpenseStats(
                total = income - expense,
                expense = expense,
                income = income
            )
        }


    override fun getExpenseStatsOfMonth(yearMonth: YearMonth, walletId: String) =
        Observables.combineLatest(
            transactionDatastore.getMonthlyExpense(yearMonth, walletId),
            transactionDatastore.getMonthlyIncome(yearMonth, walletId)
        )
            .map { (expense, income) ->
                ExpenseStats(
                    total = income - expense,
                    expense = expense,
                    income = income
                )
            }
}