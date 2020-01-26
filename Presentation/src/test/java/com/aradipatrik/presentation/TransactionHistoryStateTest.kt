package com.aradipatrik.presentation

import com.aradipatrik.presentation.datahelpers.MockDataFactory.transactionPresentation
import com.aradipatrik.presentation.viewmodels.history.TransactionHistoryState
import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.junit.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class TransactionHistoryStateTest {
    @Test
    fun `selectedMonthAsInterval should return selected month as interval`() {
        // Arrange
        val state =
            TransactionHistoryState()
        val selectedMonthAsInterval = state.selectedMonth.toInterval()

        // Act
        val result = state.selectedMonthAsInterval

        // Assert
        expectThat(result).isEqualTo(selectedMonthAsInterval)
    }

    @Test
    fun `Transactions in a day should be correctly sorted inside datesToTransactions`() {
        val testTransactions = listOf(
            DateTime(2000, 10, 1, 1, 0),
            DateTime(2000, 10, 1, 3, 0),
            DateTime(2000, 10, 1, 2, 0)
        ).map { transactionPresentation(date = it) }

        val orderedTestTransactions = testTransactions.sortedBy { it.date }
        val state =
            TransactionHistoryState(
                transactionsOfSelectedMonth = testTransactions
            )
        val resultTransactionsInDay = state.datesToTransactions[LocalDate(2000, 10, 1)]
        expectThat(resultTransactionsInDay!!.toList())
            .isEqualTo(orderedTestTransactions)
    }

    @Test
    fun `Transactions in a month should be correctly split to days of the month`() {
        val transactionsInDayOne = listOf(
            DateTime(2000, 10, 1, 1, 0),
            DateTime(2000, 10, 1, 2, 0),
            DateTime(2000, 10, 1, 3, 0)
        ).map { transactionPresentation(date = it) }

        val transactionsInDayTwo = listOf(
            DateTime(2000, 10, 2, 1, 0),
            DateTime(2000, 10, 2, 2, 0),
            DateTime(2000, 10, 2, 3, 0)
        ).map { transactionPresentation(date = it) }

        val transactionsInDayThree = listOf(
            DateTime(2000, 10, 3, 1, 0),
            DateTime(2000, 10, 3, 2, 0),
            DateTime(2000, 10, 3, 3, 0)
        ).map { transactionPresentation(date = it) }

        val allTransactions = transactionsInDayOne + transactionsInDayTwo + transactionsInDayThree

        val state =
            TransactionHistoryState(
                transactionsOfSelectedMonth = allTransactions
            )
        val sortedTransactions = state.datesToTransactions

        expectThat(sortedTransactions[LocalDate(2000, 10, 1)]!!.toList())
            .isEqualTo(transactionsInDayOne)
        expectThat(sortedTransactions[LocalDate(2000, 10, 2)]!!.toList())
            .isEqualTo(transactionsInDayTwo)
        expectThat(sortedTransactions[LocalDate(2000, 10, 3)]!!.toList())
            .isEqualTo(transactionsInDayThree)
    }
}