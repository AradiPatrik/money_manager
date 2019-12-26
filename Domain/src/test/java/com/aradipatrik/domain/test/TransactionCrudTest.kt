package com.aradipatrik.domain.test

import com.aradipatrik.domain.executor.PostExecutionThread
import com.aradipatrik.domain.model.Transaction
import com.aradipatrik.domain.repository.TransactionRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Completable
import io.reactivex.Observable
import org.junit.Test
import com.aradipatrik.domain.test.MockDataFactory.interval
import com.aradipatrik.domain.test.MockDataFactory.string
import com.aradipatrik.domain.test.MockDataFactory.transaction
import com.aradipatrik.domain.usecase.*

class TransactionCrudTest {
    private val postExecutionThread = mockk<PostExecutionThread>()
    private val transactionRepository = mockk<TransactionRepository>()

    @Test
    fun `Add should complete`() {
        stubAddTransaction(Completable.complete())
        val addTransaction = AddTransaction(
            transactionRepository,
            postExecutionThread
        )
        addTransaction.buildUseCaseCompletable(
            AddTransaction.Params.forTransaction(
                transaction()
            )
        ).test().assertComplete()
    }

    @Test
    fun `Add should use repository`() {
        val transactionToAdd = transaction()
        val addTransaction = AddTransaction(
            transactionRepository,
            postExecutionThread
        )
        stubAddTransaction(Completable.complete())
        addTransaction.buildUseCaseCompletable(
            AddTransaction.Params.forTransaction(
                transactionToAdd
            )
        )
        verify { transactionRepository.add(transactionToAdd) }
    }

    @Test(expected = IllegalArgumentException::class)
    fun `Add should throw exception if no parameters were supplied`() {
        AddTransaction(
            transactionRepository,
            postExecutionThread
        )
            .buildUseCaseCompletable()
            .test()
    }

    @Test
    fun `Delete should complete`() {
        stubDeleteTransaction(Completable.complete())
        val deleteTransaction = DeleteTransaction(
            transactionRepository,
            postExecutionThread
        )
        deleteTransaction.buildUseCaseCompletable(
            DeleteTransaction.Params.forTransaction(
                string()
            )
        ).test().assertComplete()
    }

    @Test
    fun `Delete should use repository`() {
        stubDeleteTransaction(Completable.complete())
        val transactionIdToDelete = string()
        val deleteTransaction = DeleteTransaction(
            transactionRepository,
            postExecutionThread
        )
        deleteTransaction.buildUseCaseCompletable(
            DeleteTransaction.Params.forTransaction(
                transactionIdToDelete
            )
        )
        verify { transactionRepository.delete(transactionIdToDelete) }
    }

    @Test(expected = IllegalArgumentException::class)
    fun `Delete should throw exception if no argument was supplied`() {
        DeleteTransaction(
            transactionRepository,
            postExecutionThread
        )
            .buildUseCaseCompletable()
            .test()
    }

    @Test
    fun `Update should complete`() {
        stubUpdateTransaction(Completable.complete())
        val updateTransaction = UpdateTransaction(
            transactionRepository,
            postExecutionThread
        )
        updateTransaction.buildUseCaseCompletable(
            UpdateTransaction.Params.forTransaction(
                transaction()
            )
        ).test().assertComplete()
    }

    @Test
    fun `Update should use repository`() {
        stubUpdateTransaction(Completable.complete())
        val updateTransaction = UpdateTransaction(
            transactionRepository,
            postExecutionThread
        )
        val transactionToUpdate = transaction()
        updateTransaction.buildUseCaseCompletable(
            UpdateTransaction.Params.forTransaction(
                transactionToUpdate
            )
        )
        verify { transactionRepository.update(transactionToUpdate) }
    }

    @Test(expected = IllegalArgumentException::class)
    fun `Update should throw exception if no argumetns were supplied`() {
        UpdateTransaction(
            transactionRepository,
            postExecutionThread
        )
            .buildUseCaseCompletable()
            .test()
    }

    @Test
    fun `Get transaction should complete`() {
        stubGetAllTransactions(listOf(transaction()))
        GetTransactions(
            transactionRepository,
            postExecutionThread
        )
            .buildUseCaseObservable()
            .test()
            .assertComplete()
    }

    @Test
    fun `Get transaction should use repository`() {
        val testTransactions = listOf(transaction())
        stubGetAllTransactions(testTransactions)
        val getTransactions = GetTransactions(
            transactionRepository,
            postExecutionThread
        )
        val transactionsObservable = getTransactions.buildUseCaseObservable().test()
        verify { transactionRepository.getAll() }
        transactionsObservable.assertValue(testTransactions)
    }

    @Test
    fun `Get transaction in interval should complete`() {
        stubGetTransactionsInInterval(listOf(transaction()))
        GetTransactionsInInterval(
            transactionRepository, postExecutionThread
        ).buildUseCaseObservable(
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
            GetTransactionsInInterval(
                transactionRepository, postExecutionThread
            )
        val transactionsObservable = getTransactionsInInterval.buildUseCaseObservable(
            GetTransactionsInInterval.Params.forInterval(
                testInterval
            )
        ).test()
        verify { transactionRepository.getInInterval(testInterval) }
        transactionsObservable.assertValue(testTransactions)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `Get transaction in interval should throw exception if no arguments were supplied`() {
        GetTransactionsInInterval(
            transactionRepository,
            postExecutionThread
        )
            .buildUseCaseObservable()
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

    private fun stubGetAllTransactions(transactions: List<Transaction>) {
        every { transactionRepository.getAll() } returns Observable.just(transactions)
    }

    private fun stubGetTransactionsInInterval(transactions: List<Transaction>) {
        every { transactionRepository.getInInterval(any()) } returns
                Observable.just(transactions)
    }
}
