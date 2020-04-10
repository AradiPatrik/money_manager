package com.aradipatrik.datasource.test

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.aradipatrik.local.database.TransactionDatabase
import com.aradipatrik.local.database.common.SyncStatusConstants
import com.aradipatrik.local.database.common.SyncStatusConstants.TO_DELETE_CODE
import com.aradipatrik.local.mocks.LocalDataLayerMocks.categoryRow
import com.aradipatrik.local.mocks.LocalDataLayerMocks.transactionRow
import com.aradipatrik.local.mocks.LocalDataLayerMocks.transactionWithCategory
import com.aradipatrik.testing.CommonMocks.string
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class TransactionDaoTest {
    @get:Rule
    val instantTaskExecutionRule = InstantTaskExecutorRule()

    private val database = Room.inMemoryDatabaseBuilder(
        ApplicationProvider.getApplicationContext(),
        TransactionDatabase::class.java
    )
        .allowMainThreadQueries()
        .build()

    private val allPendingTransactions = listOf(
        transactionRow(syncStatusCode = TO_DELETE_CODE),
        transactionRow(syncStatusCode = SyncStatusConstants.TO_ADD_CODE),
        transactionRow(syncStatusCode = SyncStatusConstants.TO_UPDATE_CODE)
    )

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun `Get all transactions returns data`() {
        val transaction = transactionRow()
        database.transactionDao().insert(listOf(transaction)).blockingAwait()

        val testObserver = database.transactionDao().getAllTransactions().test()
        testObserver.assertValue(listOf(transaction))
    }

    @Test
    fun `Get pending transactions returns only pending data`() {
        val syncedTransaction = transactionRow()

        database.transactionDao().insert(listOf(syncedTransaction)).blockingAwait()
        database.transactionDao().insert(allPendingTransactions).blockingAwait()

        val testObserver = database.transactionDao().getPendingTransactions().test()
        testObserver.assertValue(allPendingTransactions)
    }

    @Test
    fun `Get in interval should return transactions in interval`() {
        val category = categoryRow(string())
        val transactionsBeforeQueried = listOf(0L, 1L, 2L).map {
            transactionRow(date = it, categoryId = category.uid)
        }
        val transactionsInsideQueried = listOf(3L, 4L, 5L).map {
            transactionRow(date = it, categoryId = category.uid)
        }
        val transactionsAfterQueried = listOf(6L, 7L, 8L).map {
            transactionRow(date = it, categoryId = category.uid)
        }

        val inInterval = transactionsInsideQueried.map {
            transactionWithCategory(it, category)
        }

        database.categoryDao().insert(listOf(category)).blockingAwait()

        database.transactionDao().insert(transactionsAfterQueried).blockingAwait()
        database.transactionDao().insert(transactionsInsideQueried).blockingAwait()
        database.transactionDao().insert(transactionsBeforeQueried).blockingAwait()

        val testObserver = database.transactionDao().getInInterval(3L, 5L).test()
        testObserver.assertValue(inInterval)
    }

    @Test
    fun `Clear pending should clear all pending transactions`() {
        val syncedTransaction = transactionRow()
        database.transactionDao().insert(allPendingTransactions + syncedTransaction)
            .blockingAwait()
        database.transactionDao().clearPending().blockingAwait()

        database.transactionDao().getPendingTransactions()
            .test()
            .assertValue(emptyList())
        database.transactionDao().getAllTransactions()
            .test()
            .assertValue(listOf(syncedTransaction))
    }

    @Test
    fun `Get last sync time should return last sync time`() {
        val syncTimes = listOf<Long>(1, 2, 3, 4, 5)
        val transactions = syncTimes.map { transactionRow(updateTimestamp = it) }
        database.transactionDao().insert(transactions).blockingAwait()
        database.transactionDao().getLastSyncTime()
            .test()
            .assertValue(syncTimes.max())
    }

    @Test
    fun `Get last sync time should complete when no sync time is the db yet`() {
        database.transactionDao().getLastSyncTime()
            .test()
            .assertNoValues()
            .assertComplete()
    }

    @Test
    fun `Set deleted should set transactions code TO_DELETE`() {
        val transactionToDelete = transactionRow()
        val transactions = listOf(transactionRow(), transactionRow())
        database.transactionDao().insert(transactions + transactionToDelete).blockingAwait()
        database.transactionDao().setDeleted(transactionToDelete.uid).blockingAwait()
        database.transactionDao().getPendingTransactions()
            .test()
            .assertValue(listOf(transactionToDelete.copy(syncStatusCode = TO_DELETE_CODE)))

        database.transactionDao().getAllTransactions()
            .test()
            .assertValue(
                transactions + transactionToDelete.copy(syncStatusCode = TO_DELETE_CODE)
            )
    }
}