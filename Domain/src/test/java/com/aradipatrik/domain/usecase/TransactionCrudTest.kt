package com.aradipatrik.domain.usecase

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

class TransactionCrudTest {
    private val postExecutionThread = mockk<PostExecutionThread>()
    private val transactionRepository = mockk<TransactionRepository>()

    @Test
    fun addShouldComplete() {
        stubAddTransaction(Completable.complete())
        val addTransaction = AddTransaction(transactionRepository, postExecutionThread)
        addTransaction.buildUseCaseCompletable(
            AddTransaction.Params.forTransaction(transaction())
        ).test().assertComplete()
    }

    @Test
    fun addShouldUseRepository() {
        val transactionToAdd = transaction()
        val addTransaction = AddTransaction(transactionRepository, postExecutionThread)
        stubAddTransaction(Completable.complete())
        addTransaction.buildUseCaseCompletable(
            AddTransaction.Params.forTransaction(transactionToAdd)
        )
        verify { transactionRepository.addTransaction(transactionToAdd) }
    }

    @Test(expected = IllegalArgumentException::class)
    fun addShouldThrowExceptionIfNoParametersWereSupplied() {
        AddTransaction(transactionRepository, postExecutionThread)
            .buildUseCaseCompletable()
            .test()
    }

    @Test
    fun deleteShouldComplete() {
        stubDeleteTransaction(Completable.complete())
        val deleteTransaction = DeleteTransaction(transactionRepository, postExecutionThread)
        deleteTransaction.buildUseCaseCompletable(
            DeleteTransaction.Params.forTransaction(string())
        ).test().assertComplete()
    }

    @Test
    fun deleteShouldUseRepository() {
        stubDeleteTransaction(Completable.complete())
        val transactionIdToDelete = string()
        val deleteTransaction = DeleteTransaction(transactionRepository, postExecutionThread)
        deleteTransaction.buildUseCaseCompletable(
            DeleteTransaction.Params.forTransaction(transactionIdToDelete)
        )
        verify { transactionRepository.deleteTransaction(transactionIdToDelete) }
    }

    @Test(expected = IllegalArgumentException::class)
    fun deleteShouldThrowExceptionIfNoArgumentSupplied() {
        DeleteTransaction(transactionRepository, postExecutionThread)
            .buildUseCaseCompletable()
            .test()
    }

    @Test
    fun updateShouldComplete() {
        stubUpdateTransaction(Completable.complete())
        val updateTransaction = UpdateTransaction(transactionRepository, postExecutionThread)
        updateTransaction.buildUseCaseCompletable(
            UpdateTransaction.Params.forTransaction(transaction())
        ).test().assertComplete()
    }

    @Test
    fun updateShouldUseRepository() {
        stubUpdateTransaction(Completable.complete())
        val updateTransaction = UpdateTransaction(transactionRepository, postExecutionThread)
        val transactionToUpdate = transaction()
        updateTransaction.buildUseCaseCompletable(
            UpdateTransaction.Params.forTransaction(transactionToUpdate)
        )
        verify { transactionRepository.updateTransaction(transactionToUpdate) }
    }

    @Test(expected = IllegalArgumentException::class)
    fun updateShouldThrowExceptionIfNoArgumentSupplied() {
        UpdateTransaction(transactionRepository, postExecutionThread)
            .buildUseCaseCompletable()
            .test()
    }

    @Test
    fun getTransactionsShouldComplete() {
        stubGetAllTransactions(listOf(transaction()))
        GetTransactions(transactionRepository, postExecutionThread)
            .buildUseCaseObservable()
            .test()
            .assertComplete()
    }

    @Test
    fun getTransactionsShouldUseRepository() {
        val testTransactions = listOf(transaction())
        stubGetAllTransactions(testTransactions)
        val getTransactions = GetTransactions(transactionRepository, postExecutionThread)
        val transactionsObservable = getTransactions.buildUseCaseObservable().test()
        verify { transactionRepository.getAllTransactions() }
        transactionsObservable.assertValue(testTransactions)
    }

    @Test
    fun getTransactionsInIntervalShouldComplete() {
        stubGetTransactionsInInterval(listOf(transaction()))
        GetTransactionsInInterval(
            transactionRepository, postExecutionThread
        ).buildUseCaseObservable(GetTransactionsInInterval.Params.forInterval(interval()))
            .test()
            .assertComplete()
    }

    @Test
    fun getTransactionsInIntervalShouldUseRepository() {
        val testTransactions = listOf(transaction())
        val testInterval = interval()
        stubGetTransactionsInInterval(testTransactions)
        val getTransactionsInInterval = GetTransactionsInInterval(
            transactionRepository, postExecutionThread
        )
        val transactionsObservable = getTransactionsInInterval.buildUseCaseObservable(
            GetTransactionsInInterval.Params.forInterval(testInterval)
        ).test()
        verify { transactionRepository.getTransactionsInInterval(testInterval) }
        transactionsObservable.assertValue(testTransactions)
    }

    @Test(expected = IllegalArgumentException::class)
    fun getTransactionsShouldThrowExceptionsIfNoArgumentsSupplied() {
        GetTransactionsInInterval(transactionRepository, postExecutionThread)
            .buildUseCaseObservable()
            .test()
    }

    private fun stubAddTransaction(completable: Completable) {
        every { transactionRepository.addTransaction(any()) } returns completable
    }

    private fun stubDeleteTransaction(completable: Completable) {
        every { transactionRepository.deleteTransaction(any()) } returns completable
    }

    private fun stubUpdateTransaction(completable: Completable) {
        every { transactionRepository.updateTransaction(any()) } returns completable
    }

    private fun stubGetAllTransactions(transactions: List<Transaction>) {
        every { transactionRepository.getAllTransactions() } returns Observable.just(transactions)
    }

    private fun stubGetTransactionsInInterval(transactions: List<Transaction>) {
        every { transactionRepository.getTransactionsInInterval(any()) } returns
                Observable.just(transactions)
    }
}
