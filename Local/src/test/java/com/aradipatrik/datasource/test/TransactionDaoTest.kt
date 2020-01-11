package com.aradipatrik.datasource.test

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import com.aradipatrik.datasource.test.TransactionRowFactory.categoryRow
import com.aradipatrik.datasource.test.TransactionRowFactory.transactionRow
import com.aradipatrik.datasource.test.TransactionRowFactory.transactionWithCategory
import com.aradipatrik.local.database.TransactionDatabase
import com.aradipatrik.local.database.common.SyncStatusConstants
import com.aradipatrik.testing.DomainLayerMocks.string
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class TransactionDaoTest {
    @get:Rule
    val instantTaskExecutionRule = InstantTaskExecutorRule()

    private val database = Room.inMemoryDatabaseBuilder(
        RuntimeEnvironment.application.applicationContext,
        TransactionDatabase::class.java)
        .allowMainThreadQueries()
        .build()

    private val allPendingTransactions = listOf(
        transactionRow(syncStatusCode = SyncStatusConstants.TO_DELETE_CODE),
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
    fun `Set deleted should set transactions code TO_DELETE`() {
        val transactionToDelete = transactionRow()
        val transactions = listOf(transactionRow(), transactionToDelete, transactionRow())
        database.transactionDao().insert(transactions)
    }
}