package com.aradipatrik.domain.test

import com.aradipatrik.domain.interactor.transaction.AddTransactionInteractor
import com.aradipatrik.domain.interactor.transaction.DeleteTransactionInteractor
import com.aradipatrik.domain.interactor.transaction.GetTransactionsInIntervalInteractor
import com.aradipatrik.domain.interactor.transaction.UpdateTransactionInteractor
import com.aradipatrik.domain.interfaces.data.TransactionRepository
import com.aradipatrik.domain.interfaces.data.WalletRepository
import com.aradipatrik.domain.mocks.DomainLayerMocks.transaction
import com.aradipatrik.domain.mocks.DomainLayerMocks.wallet
import com.aradipatrik.domain.model.Transaction
import com.aradipatrik.domain.model.Wallet
import com.aradipatrik.testing.CommonMocks.interval
import com.aradipatrik.testing.CommonMocks.string
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject

class TransactionCrudTest: KoinTest {
    private val testModule = module {
        single<TransactionRepository> { mockk() }
        single<WalletRepository> { mockk() }
        single { AddTransactionInteractor(get(), get()) }
        single { DeleteTransactionInteractor(get()) }
        single { GetTransactionsInIntervalInteractor(get(), get()) }
        single { UpdateTransactionInteractor(get(), get()) }
    }

    private val transactionRepository: TransactionRepository by inject()
    private val walletRepository: WalletRepository by inject()
    private val addTransaction: AddTransactionInteractor by inject()
    private val deleteTransaction: DeleteTransactionInteractor by inject()
    private val getTransactionInInterval: GetTransactionsInIntervalInteractor by inject()
    private val updateTransaction: UpdateTransactionInteractor by inject()

    @Before
    fun setup() {
        startKoin {
            modules(testModule)
        }
    }

    @After
    fun teardown() {
        stopKoin()
    }

    @Test
    fun `Add should complete`() {
        stubAddTransaction(Completable.complete())
        stubGetSelectedWallet(wallet())
        addTransaction.get(
            AddTransactionInteractor.Params.forTransaction(
                transaction()
            )
        ).test().assertComplete()
    }

    @Test
    fun `Add should use repository`() {
        val transactionToAdd = transaction()
        val wallet = wallet()
        stubAddTransaction(Completable.complete())
        stubGetSelectedWallet(wallet)
        addTransaction.get(
            AddTransactionInteractor.Params.forTransaction(
                transactionToAdd
            )
        ).test()

        verify { transactionRepository.add(transactionToAdd, wallet.id) }
    }

    @Test(expected = IllegalArgumentException::class)
    fun `Add should throw exception if no parameters were supplied`() {
        addTransaction.get().test()
    }

    @Test
    fun `Delete should complete`() {
        stubGetSelectedWallet(wallet())
        stubDeleteTransaction(Completable.complete())
        deleteTransaction.get(
            DeleteTransactionInteractor.Params.forTransaction(
                string()
            )
        ).test().assertComplete()
    }

    @Test
    fun `Delete should use repository`() {
        stubDeleteTransaction(Completable.complete())
        val transactionIdToDelete = string()
        val deleteTransaction =
            DeleteTransactionInteractor(
                transactionRepository
            )
        deleteTransaction.get(
            DeleteTransactionInteractor.Params.forTransaction(
                transactionIdToDelete
            )
        )
        verify { transactionRepository.delete(transactionIdToDelete) }
    }

    @Test(expected = IllegalArgumentException::class)
    fun `Delete should throw exception if no argument was supplied`() {
        DeleteTransactionInteractor(
            transactionRepository
        )
            .get()
            .test()
    }

    @Test
    fun `Update should complete`() {
        stubUpdateTransaction(Completable.complete())
        stubGetSelectedWallet(wallet())
        updateTransaction.get(
            UpdateTransactionInteractor.Params.forTransaction(transaction())
        )
            .test()
            .assertComplete()
    }

    @Test
    fun `Update should use repository`() {
        stubUpdateTransaction(Completable.complete())
        val transactionToUpdate = transaction()
        val testWallet = wallet()
        stubGetSelectedWallet(testWallet)
        updateTransaction.get(
            UpdateTransactionInteractor.Params.forTransaction(transactionToUpdate)
        ).test()
        verify { transactionRepository.update(transactionToUpdate, testWallet.id) }
    }

    @Test(expected = IllegalArgumentException::class)
    fun `Update should throw exception if no argumetns were supplied`() {
        updateTransaction.get().test()
    }

    @Test
    fun `Get transaction in interval should complete`() {
        stubGetTransactionsInInterval(listOf(transaction()))
        stubGetSelectedWallet(wallet())
        getTransactionInInterval.get(
            GetTransactionsInIntervalInteractor.Params.forInterval(interval())
        )
            .test()
            .assertComplete()
    }

    @Test
    fun `Get transaction in interval should use repository`() {
        val testTransactions = listOf(transaction())
        val testInterval = interval()
        val testWallet = wallet()
        stubGetSelectedWallet(testWallet)
        stubGetTransactionsInInterval(testTransactions)
        val transactionsObservable = getTransactionInInterval.get(
            GetTransactionsInIntervalInteractor.Params.forInterval(
                testInterval
            )
        ).test()
        verify { transactionRepository.getInInterval(testInterval, testWallet.id) }
        transactionsObservable.assertValue(testTransactions)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `Get transaction in interval should throw exception if no arguments were supplied`() {
        getTransactionInInterval.get().test()
    }

    private fun stubAddTransaction(completable: Completable) {
        every { transactionRepository.add(any(), any()) } returns completable
    }

    private fun stubDeleteTransaction(completable: Completable) {
        every { transactionRepository.delete(any()) } returns completable
    }

    private fun stubUpdateTransaction(completable: Completable) {
        every { transactionRepository.update(any(), any()) } returns completable
    }

    private fun stubGetTransactionsInInterval(transactions: List<Transaction>) {
        every { transactionRepository.getInInterval(any(), any()) } returns
                Observable.just(transactions)
    }

    private fun stubGetSelectedWallet(selectedWallet: Wallet) {
        every { walletRepository.getSelectedWallet() } returns Single.just(selectedWallet)
    }
}
