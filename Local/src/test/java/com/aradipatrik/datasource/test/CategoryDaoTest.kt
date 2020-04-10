package com.aradipatrik.datasource.test

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.aradipatrik.local.database.TransactionDatabase
import com.aradipatrik.local.database.category.CategoryRow
import com.aradipatrik.local.database.common.SyncStatusConstants.TO_ADD_CODE
import com.aradipatrik.local.database.common.SyncStatusConstants.TO_DELETE_CODE
import com.aradipatrik.local.database.common.SyncStatusConstants.TO_UPDATE_CODE
import com.aradipatrik.local.mocks.LocalDataLayerMocks.categoryRow
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@Suppress("SameParameterValue")
@RunWith(RobolectricTestRunner::class)
class CategoryDaoTest {
    @get:Rule
    val instantTaskExecutionRule = InstantTaskExecutorRule()

    private val database = Room.inMemoryDatabaseBuilder(
        ApplicationProvider.getApplicationContext(),
        TransactionDatabase::class.java
    )
        .allowMainThreadQueries()
        .build()

    private val pendingCategoryInstances = listOf(
        categoryRow(syncStatusCode = TO_DELETE_CODE),
        categoryRow(syncStatusCode = TO_ADD_CODE),
        categoryRow(syncStatusCode = TO_UPDATE_CODE)
    )

    private val testCategory = categoryRow()
    private val syncedCategory = categoryRow()
    private val categoryDao get() = database.categoryDao()
    private val categoryToDelete = categoryRow()
    private val randomCategories = createNRandomCategories(2)

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun `Get all categories returns data`() {
        initDbWithCategories(testCategory)
        val testObserver = categoryDao.getAllCategories().test()
        testObserver.assertValue(listOf(testCategory)).dispose()
    }

    @Test
    fun `Get pending categories returns only pending data`() {
        initDbWithCategories(pendingCategoryInstances + syncedCategory)
        val testObserver = categoryDao.getPendingCategories().test()
        testObserver.assertValue(pendingCategoryInstances)
    }

    @Test
    fun `Clear pending should clear all pending categories`() {
        initDbWithCategories(pendingCategoryInstances + syncedCategory)
        categoryDao.clearPending().blockingAwait()
        assertOnlySyncedCategoryRemains()
    }

    private fun assertOnlySyncedCategoryRemains() {
        categoryDao.getPendingCategories()
            .test()
            .assertValue(emptyList())
            .dispose()
        categoryDao.getAllCategories()
            .test()
            .assertValue(listOf(syncedCategory))
            .dispose()
    }

    @Test
    fun `Get last sync time should return last sync time`() {
        initDbWithCategories(createCategoriesWithSyncTimestampsOf(1, 2, 3, 4, 5))
        assertLastSyncTimeIs(5)
    }

    private fun assertLastSyncTimeIs(lastSyncTime: Long) {
        categoryDao.getLastSyncTime()
            .test()
            .assertValue(lastSyncTime)
            .dispose()
    }

    private fun createCategoriesWithSyncTimestampsOf(vararg syncTimes: Long) =
        syncTimes.map { categoryRow(updateTimestamp = it) }

    @Test
    fun `Get last sync time should complete when no sync time is the db yet`() {
        categoryDao.getLastSyncTime()
            .test()
            .assertNoValues()
            .assertComplete()
            .dispose()
    }

    @Test
    fun `Set deleted should set category sync code TO_DELETE`() {
        initDbWithCategories(randomCategories + categoryToDelete)
        categoryDao.setDeleted(categoryToDelete.uid).blockingAwait()
        assertDeletedCategoryBecamePendingWithDeletedSyncCode()
        assertGetAllCategoriesContainsDeletedCategory()
    }

    private fun assertGetAllCategoriesContainsDeletedCategory() {
        categoryDao.getAllCategories()
            .test()
            .assertValue(
                randomCategories + categoryToDelete.copy(syncStatusCode = TO_DELETE_CODE)
            )
            .dispose()
    }

    private fun assertDeletedCategoryBecamePendingWithDeletedSyncCode() {
        categoryDao.getPendingCategories()
            .test()
            .assertValue(listOf(categoryToDelete.copy(syncStatusCode = TO_DELETE_CODE)))
            .dispose()
    }

    private fun initDbWithCategories(category: CategoryRow) = initDbWithCategories(listOf(category))

    private fun initDbWithCategories(categories: List<CategoryRow>) = categoryDao
        .insert(categories)
        .blockingAwait()

    private fun createNRandomCategories(n: Int) = generateSequence { categoryRow() }
        .take(n)
        .toList()
}
