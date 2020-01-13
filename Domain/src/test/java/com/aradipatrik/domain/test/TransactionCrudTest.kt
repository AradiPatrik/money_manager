package com.aradipatrik.domain.test

import com.aradipatrik.domain.model.Transaction
import com.aradipatrik.domain.repository.TransactionRepository
import com.aradipatrik.domain.usecase.AddTransaction
import com.aradipatrik.domain.usecase.DeleteTransaction
import com.aradipatrik.domain.usecase.GetTransactionsInInterval
import com.aradipatrik.domain.usecase.UpdateTransaction
import com.aradipatrik.testing.DomainLayerMocks.interval
import com.aradipatrik.testing.DomainLayerMocks.string
import com.aradipatrik.testing.DomainLayerMocks.transaction
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Completable
import io.reactivex.Observable
import org.junit.Test

class TransactionCrudTest {
    private val transactionRepository = mockk<TransactionRepository>()

    @Test
    fun `Add should complete`() {
        stubAddTransaction(Completable.complete())
        val addTransaction = AddTransaction(transactionRepository)
        addTransaction.get(
            AddTransaction.Params.forTransaction(
                transaction()
            )
        ).test().assertComplete()
    }

    @Test
    fun `Add should use repository`() {
        val transactionToAdd = transaction()
        val addTransaction = AddTransaction(transactionRepository)
        stubAddTransaction(Completable.complete())
        addTransaction.get(
            AddTransaction.Params.forTransaction(
                transactionToAdd
            )
        )
        verify { transactionRepository.add(transactionToAdd) }
    }

    @Test(expected = IllegalArgumentException::class)
    fun `Add should throw exception if no parameters were supplied`() {
        AddTransaction(transactionRepository)
            .get()
            .test()
    }

    @Test
    fun `Delete should complete`() {
        stubDeleteTransaction(Completable.complete())
        val deleteTransaction = DeleteTransaction(transactionRepository)
        deleteTransaction.get(
            DeleteTransaction.Params.forTransaction(
                string()
            )
        ).test().assertComplete()
    }

    @Test
    fun `Delete should use repository`() {
        stubDeleteTransaction(Completable.complete())
        val transactionIdToDelete = string()
        val deleteTransaction = DeleteTransaction(transactionRepository)
        deleteTransaction.get(
            DeleteTransaction.Params.forTransaction(
                transactionIdToDelete
            )
        )
        verify { transactionRepository.delete(transactionIdToDelete) }
    }

    @Test(expected = IllegalArgumentException::class)
    fun `Delete should throw exception if no argument was supplied`() {
        DeleteTransaction(transactionRepository)
            .get()
            .test()
    }

    @Test
    fun `Update should complete`() {
        stubUpdateTransaction(Completable.complete())
        val updateTransaction = UpdateTransaction(transactionRepository)
        updateTransaction.get(
            UpdateTransaction.Params.forTransaction(
                transaction()
            )
        ).test().assertComplete()
    }

    @Test
    fun `Update should use repository`() {
        stubUpdateTransaction(Completable.complete())
        val updateTransaction = UpdateTransaction(transactionRepository)
        val transactionToUpdate = transaction()
        updateTransaction.get(
            UpdateTransaction.Params.forTransaction(
                transactionToUpdate
            )
        )
        verify { transactionRepository.update(transactionToUpdate) }
    }

    @Test(expected = IllegalArgumentException::class)
    fun `Update should throw exception if no argumetns were supplied`() {
        UpdateTransaction(transactionRepository)
            .get()
            .test()
    }

    @Test
    fun `Get transaction in interval should complete`() {
        stubGetTransactionsInInterval(listOf(transaction()))
        GetTransactionsInInterval(transactionRepository).get(
            GetTransactionsInInterval.Params.forInterval(
                interval()
            )
        )
            .test()
            .assertComplete()
    }

    @Test
    fun `Get transaction in interval should use repository`() {
        val testTransactions = listOf(transaction())
        val testInterval = interval()
        stubGetTransactionsInInterval(testTransactions)
        val getTransactionsInInterval =
            GetTransactionsInInterval(transactionRepository)
        val transactionsObservable = getTransactionsInInterval.get(
            GetTransactionsInInterval.Params.forInterval(
                testInterval
            )
        ).test()
        verify { transactionRepository.getInInterval(testInterval) }
        transactionsObservable.assertValue(testTransactions)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `Get transaction in interval should throw exception if no arguments were supplied`() {
        GetTransactionsInInterval(transactionRepository)
            .get()
            .test()
    }

    private fun stubAddTransaction(completable: Completable) {
        every { transactionRepository.add(any()) } returns completable
    }

    private fun stubDeleteTransaction(completable: Completable) {
        every { transactionRepository.delete(any()) } returns completable
    }

    private fun stubUpdateTransaction(completable: Completable) {
        every { transactionRepository.update(any()) } returns completable
    }

    private fun stubGetTransactionsInInterval(transactions: List<Transaction>) {
        every { transactionRepository.getInInterval(any()) } returns
                Observable.just(transactions)
    }
}
