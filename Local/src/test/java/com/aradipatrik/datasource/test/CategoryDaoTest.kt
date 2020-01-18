package com.aradipatrik.datasource.test

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import com.aradipatrik.datasource.test.TransactionRowFactory.categoryRow
import com.aradipatrik.local.database.TransactionDatabase
import com.aradipatrik.local.database.common.SyncStatusConstants
import com.aradipatrik.local.database.common.SyncStatusConstants.TO_DELETE_CODE
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class CategoryDaoTest {
    @get:Rule
    val instantTaskExecutionRule = InstantTaskExecutorRule()

    private val database = Room.inMemoryDatabaseBuilder(
        RuntimeEnvironment.application.applicationContext,
        TransactionDatabase::class.java)
        .allowMainThreadQueries()
        .build()

    private val allPendingCategories = listOf(
        categoryRow(syncStatusCode = TO_DELETE_CODE),
        categoryRow(syncStatusCode = SyncStatusConstants.TO_ADD_CODE),
        categoryRow(syncStatusCode = SyncStatusConstants.TO_UPDATE_CODE)
    )

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun `Get all categories returns data`() {
        val category = categoryRow()
        database.categoryDao().insert(listOf(category)).blockingAwait()

        val testObserver = database.categoryDao().getAllCategories().test()
        testObserver.assertValue(listOf(category))
    }

    @Test
    fun `Get pending categories returns only pending data`() {
        val syncedCategory = categoryRow()

        database.categoryDao().insert(listOf(syncedCategory)).blockingAwait()
        database.categoryDao().insert(allPendingCategories).blockingAwait()

        val testObserver = database.categoryDao().getPendingCategories().test()
        testObserver.assertValue(allPendingCategories)
    }

    @Test
    fun `Clear pending should clear all pending categories`() {
        val syncedCategory = categoryRow()
        database.categoryDao().insert(allPendingCategories + syncedCategory)
            .blockingAwait()
        database.categoryDao().clearPending().blockingAwait()

        database.categoryDao().getPendingCategories()
            .test()
            .assertValue(emptyList())
        database.categoryDao().getAllCategories()
            .test()
            .assertValue(listOf(syncedCategory))
    }

    @Test
    fun `Get last sync time should return last sync time`() {
        val syncTimes = listOf<Long>(1, 2, 3, 4, 5)
        val categories = syncTimes.map { categoryRow(updateTimestamp = it) }
        database.categoryDao().insert(categories).blockingAwait()
        database.categoryDao().getLastSyncTime()
            .test()
            .assertValue(syncTimes.max())
    }

    @Test
    fun `Get last sync time should complete when no sync time is the db yet`() {
        database.categoryDao().getLastSyncTime()
            .test()
            .assertNoValues()
            .assertComplete()
    }

    @Test
    fun `Set deleted should set category sync code TO_DELETE`() {
        val categoryToDelete = categoryRow()
        val categories = listOf(categoryRow(), categoryRow())
        database.categoryDao().insert(categories + categoryToDelete).blockingAwait()
        database.categoryDao().setDeleted(categoryToDelete.uid).blockingAwait()
        database.categoryDao().getPendingCategories()
            .test()
            .assertValue(listOf(categoryToDelete.copy(syncStatusCode = TO_DELETE_CODE)))

        database.categoryDao().getAllCategories()
            .test()
            .assertValue(
                categories + categoryToDelete.copy(syncStatusCode = TO_DELETE_CODE))
    }
}