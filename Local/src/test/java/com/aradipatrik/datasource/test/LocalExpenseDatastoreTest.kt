package com.aradipatrik.datasource.test

import com.aradipatrik.data.datastore.stats.LocalExpenseStatsDatastore
import com.aradipatrik.data.datastore.transaction.LocalTransactionDatastore
import com.aradipatrik.data.mocks.DataLayerMocks.transactionWithIds
import org.joda.time.YearMonth
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.test.inject
import org.robolectric.RobolectricTestRunner
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import kotlin.math.abs

@RunWith(RobolectricTestRunner::class)
class LocalExpenseDatastoreTest : BaseRoomTest() {
    private val transactionDatastore: LocalTransactionDatastore by inject()
    private val expenseStatsDatastore: LocalExpenseStatsDatastore by inject()
    private val testWalletId = "testWalletId"

    @Test
    fun getAllTimeExpenseShouldReturnTheSumOfAllExpensesAndIgnoreIncomeTransactions() {
        val amountOfOneExpense = -50
        val expenseCount = 10
        val incomeCount = 10

        val testExpenses = generateSequence { amountOfOneExpense }
            .map { transactionWithIds(walletId = testWalletId, amount = it) }
            .take(expenseCount)
            .toList()

        val testIncomes = generateSequence { -amountOfOneExpense }
            .map { transactionWithIds(walletId = testWalletId, amount = it) }
            .take(incomeCount)
            .toList()

        testExpenses.forEach { transactionDatastore.add(it).blockingAwait() }
        testIncomes.forEach { transactionDatastore.add(it).blockingAwait() }

        val first = expenseStatsDatastore.getAllTimeExpense(walletId = testWalletId).blockingFirst()
        expectThat(first).isEqualTo(abs(amountOfOneExpense * expenseCount))
    }

    @Test
    fun getAllTimeIncomeShouldReturnTheSumOfAllIncomeAndIgnoreExpenseTransactions() {
        val amountOfOneIncome = 50
        val expenseCount = 10
        val incomeCount = 10

        val testExpenses = generateSequence { amountOfOneIncome }
            .map { transactionWithIds(walletId = testWalletId, amount = it) }
            .take(expenseCount)
            .toList()

        val testIncomes = generateSequence { -amountOfOneIncome }
            .map { transactionWithIds(walletId = testWalletId, amount = it) }
            .take(incomeCount)
            .toList()

        testExpenses.forEach { transactionDatastore.add(it).blockingAwait() }
        testIncomes.forEach { transactionDatastore.add(it).blockingAwait() }

        val first = expenseStatsDatastore.getAllTimeIncome(walletId = testWalletId).blockingFirst()
        expectThat(first).isEqualTo(amountOfOneIncome * expenseCount)
    }

    @Test
    fun getAllExpenseInIntervalShouldReturnSumOfAllExpensesInInterval() {
        val amountOfOneExpense = -50
        val expenseCount = 10
        val incomeCount = 10

        val monthList = generateSequence(1) { it + 1 }
            .map {
                YearMonth(1995, it).toLocalDate(2).toDateTimeAtCurrentTime()
            }
            .take(3)
            .toList()

        val testExpenses = monthList
            .flatMap {
                generateSequence {
                    transactionWithIds(walletId = testWalletId, amount = amountOfOneExpense, date = it)
                }
                    .take(expenseCount)
                    .toList()
            }
            .toList()

        val testIncomes = monthList
            .flatMap {
                generateSequence {
                    transactionWithIds(walletId = testWalletId, amount = -amountOfOneExpense, date = it)
                }
                    .take(incomeCount)
                    .toList()
            }
            .toList()

        testExpenses.forEach { transactionDatastore.add(it).blockingAwait() }
        testIncomes.forEach { transactionDatastore.add(it).blockingAwait() }

        val amount = expenseStatsDatastore.getMonthlyIncome(
            YearMonth(1995, 2),
            walletId = testWalletId
        ).blockingFirst()

        expectThat(amount).isEqualTo(abs(amountOfOneExpense * expenseCount))
    }

    @Test
    fun getAllIncomeInIntervalShouldReturnSumOfAllIncomesInInterval() {
        val amountOfOneIncome = 50
        val expenseCount = 10
        val incomeCount = 10

        val monthList = generateSequence(1) { it + 1 }
            .map {
                YearMonth(1995, it).toLocalDate(2).toDateTimeAtCurrentTime()
            }
            .take(3)
            .toList()

        val testExpenses = monthList
            .flatMap {
                generateSequence {
                    transactionWithIds(walletId = testWalletId, amount = -amountOfOneIncome, date = it)
                }
                    .take(expenseCount)
                    .toList()
            }
            .toList()

        val testIncomes = monthList
            .flatMap {
                generateSequence {
                    transactionWithIds(walletId = testWalletId, amount = amountOfOneIncome, date = it)
                }
                    .take(incomeCount)
                    .toList()
            }
            .toList()

        testExpenses.forEach { transactionDatastore.add(it).blockingAwait() }
        testIncomes.forEach { transactionDatastore.add(it).blockingAwait() }

        val amount = expenseStatsDatastore.getMonthlyIncome(
            YearMonth(1995, 2),
            walletId = testWalletId
        ).blockingFirst()

        expectThat(amount).isEqualTo(amountOfOneIncome * incomeCount)
    }
}
