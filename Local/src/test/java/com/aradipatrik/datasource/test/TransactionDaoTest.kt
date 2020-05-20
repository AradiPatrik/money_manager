package com.aradipatrik.datasource.test

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.aradipatrik.local.database.TransactionDatabase
import com.aradipatrik.local.database.common.SyncStatusConstants
import com.aradipatrik.local.database.common.SyncStatusConstants.TO_DELETE_CODE
import com.aradipatrik.local.database.model.transaction.TransactionDao
import com.aradipatrik.local.mocks.LocalMocks.categoryRow
import com.aradipatrik.local.mocks.LocalMocks.transactionRow
import com.aradipatrik.local.mocks.LocalMocks.transactionWithCategory
import com.aradipatrik.testing.CommonMocks.string
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.test.inject
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class TransactionDaoTest : BaseRoomTest() {
    private val allPendingTransactions = listOf(
        transactionRow(syncStatusCode = TO_DELETE_CODE),
        transactionRow(syncStatusCode = SyncStatusConstants.TO_ADD_CODE),
        transactionRow(syncStatusCode = SyncStatusConstants.TO_UPDATE_CODE)
    )
    
    private val transactionDao: TransactionDao by inject()
    
    @Test
    fun `Get all transactions returns data`() {
        val transaction = transactionRow()
        transactionDao.insert(listOf(transaction)).blockingAwait()

        val testObserver = transactionDao.getAllTransactions().test()
        testObserver.assertValue(listOf(transaction))
    }

    @Test
    fun `Get pending transactions returns only pending data`() {
        val syncedTransaction = transactionRow()

        transactionDao.insert(listOf(syncedTransaction)).blockingAwait()
        transactionDao.insert(allPendingTransactions).blockingAwait()

        val testObserver = transactionDao.getPendingTransactions().test()
        testObserver.assertValue(allPendingTransactions)
    }

    @Test
    fun `Get in interval should return transactions in interval`() {
        val category = categoryRow(string())
        val walletId = "testWalletId"
        val transactionsBeforeQueried = listOf(0L, 1L, 2L).map {
            transactionRow(date = it, categoryId = category.uid, walletId = walletId)
        }
        val transactionsInsideQueried = listOf(3L, 4L, 5L).map {
            transactionRow(date = it, categoryId = category.uid, walletId = walletId)
        }
        val transactionsAfterQueried = listOf(6L, 7L, 8L).map {
            transactionRow(date = it, categoryId = category.uid, walletId = walletId)
        }
        val transactionInsideQueriedButNotInWallet = transactionRow(
            date = 4L, categoryId = category.uid, walletId = string()
        )

        val inInterval = transactionsInsideQueried.map {
            transactionWithCategory(it, category)
        }

        database.categoryDao().insert(listOf(category)).blockingAwait()

        transactionDao.insert(transactionsAfterQueried).blockingAwait()
        transactionDao.insert(transactionsInsideQueried).blockingAwait()
        transactionDao.insert(transactionsBeforeQueried).blockingAwait()
        transactionDao.insert(listOf(transactionInsideQueriedButNotInWallet))
            .blockingAwait()

        val testObserver = transactionDao
            .getInInterval(3L, 5L, walletId).test()
        testObserver.assertValue(inInterval)
    }

    @Test
    fun `Clear pending should clear all pending transactions`() {
        val syncedTransaction = transactionRow()
        transactionDao.insert(allPendingTransactions + syncedTransaction)
            .blockingAwait()
        transactionDao.clearPending().blockingAwait()

        transactionDao.getPendingTransactions()
            .test()
            .assertValue(emptyList())
        transactionDao.getAllTransactions()
            .test()
            .assertValue(listOf(syncedTransaction))
    }

    @Test
    fun `Get last sync time should return last sync time`() {
        val syncTimes = listOf<Long>(1, 2, 3, 4, 5)
        val transactions = syncTimes.map { transactionRow(updateTimestamp = it) }
        transactionDao.insert(transactions).blockingAwait()
        transactionDao.getLastSyncTime()
            .test()
            .assertValue(syncTimes.max())
    }

    @Test
    fun `Get last sync time should complete when no sync time is the db yet`() {
        transactionDao.getLastSyncTime()
            .test()
            .assertNoValues()
            .assertComplete()
    }

    @Test
    fun `Set deleted should set transactions code TO_DELETE`() {
        val transactionToDelete = transactionRow()
        val transactions = listOf(transactionRow(), transactionRow())
        transactionDao.insert(transactions + transactionToDelete).blockingAwait()
        transactionDao.setDeleted(transactionToDelete.uid).blockingAwait()
        transactionDao.getPendingTransactions()
            .test()
            .assertValue(listOf(transactionToDelete.copy(syncStatusCode = TO_DELETE_CODE)))

        transactionDao.getAllTransactions()
            .test()
            .assertValue(
                transactions + transactionToDelete.copy(syncStatusCode = TO_DELETE_CODE)
            )
    }

    @Test
    fun `Get inside wallet should return transactions inside wallet`() {
        val category = categoryRow(string())
        val transaction = transactionRow(categoryId = category.uid)
        val transaction2 = transactionRow(categoryId= category.uid)

        database.categoryDao().insert(listOf(category)).blockingAwait()
        transactionDao.insert(listOf(transaction, transaction2)).blockingAwait()
        transactionDao.getInWallet(transaction.walletId)
            .test()
            .assertValue(listOf(transactionWithCategory(transaction, category)))
    }
}