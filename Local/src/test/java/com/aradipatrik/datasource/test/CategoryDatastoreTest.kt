package com.aradipatrik.datasource.test

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.aradipatrik.data.datastore.category.LocalCategoryDatastore
import com.aradipatrik.data.mapper.SyncStatus
import com.aradipatrik.data.mocks.DataLayerMocks.categoryEntity
import com.aradipatrik.local.database.RoomLocalCategoryDatastore
import com.aradipatrik.local.database.TransactionDatabase
import com.aradipatrik.local.database.mapper.CategoryRowMapper
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.*

@RunWith(RobolectricTestRunner::class)
class CategoryDatastoreTest {
    @get:Rule
    val instantTaskExecutionRule = InstantTaskExecutorRule()

    private val database = Room.inMemoryDatabaseBuilder(
        ApplicationProvider.getApplicationContext(),
        TransactionDatabase::class.java
    )
        .allowMainThreadQueries()
        .build()

    private lateinit var datastore: LocalCategoryDatastore
    private val categoryRowMapper = CategoryRowMapper()
    private val categoryEntitiesWithAllSyncStatuses =
        EnumSet.allOf(SyncStatus::class.java).map { categoryEntity(syncStatus = it) }

    @Before
    fun setup() {
        datastore = RoomLocalCategoryDatastore(database.categoryDao(), categoryRowMapper)
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun `Update with should insert the row representations of the entities passed`() {
        datastore.updateWith(categoryEntitiesWithAllSyncStatuses)
            .test()
            .assertComplete()

        datastore.getAll()
            .test()
            .assertValue(categoryEntitiesWithAllSyncStatuses)
    }

    @Test
    fun `Get pending should return those categories which are not synced`() {
        datastore.updateWith(categoryEntitiesWithAllSyncStatuses).blockingAwait()
        datastore.getPending()
            .test()
            .assertValue(
                categoryEntitiesWithAllSyncStatuses.filter { it.syncStatus != SyncStatus.Synced }
            )
    }

    @Test
    fun `Clear pending should remove all categories except those that are synced`() {
        datastore.updateWith(categoryEntitiesWithAllSyncStatuses).blockingAwait()
        datastore.clearPending()
            .test()
            .assertComplete()

        datastore.getPending()
            .test()
            .assertValue(emptyList())

        datastore.getAll()
            .test()
            .assertValue(
                categoryEntitiesWithAllSyncStatuses.filter { it.syncStatus == SyncStatus.Synced }
            )
    }

    @Test
    fun `Last sync time should return last sync time`() {
        val syncTimes = listOf<Long>(1, 2, 3, 4, 5)
        val syncedCategories = syncTimes.map { categoryEntity(updatedTimeStamp = it) }
        datastore.updateWith(syncedCategories).blockingAwait()

        datastore.getLastSyncTime()
            .test()
            .assertValue(syncTimes.max())
    }

    @Test
    fun `Add should insert entity with to add sync status`() {
        categoryEntitiesWithAllSyncStatuses.forEach {
            datastore.add(it)
                .test()
                .assertComplete()
        }

        datastore.getAll()
            .test()
            .assertValue(
                categoryEntitiesWithAllSyncStatuses.map { it.copy(syncStatus = SyncStatus.ToAdd) }
            )
    }

    @Test
    fun `Update should update entity sync status with to update, and should update them`() {
        val updatedName = "updatedName"
        datastore.updateWith(categoryEntitiesWithAllSyncStatuses).blockingAwait()
        categoryEntitiesWithAllSyncStatuses.forEach {
            datastore.update(it.copy(name = updatedName))
                .test()
                .assertComplete()
        }

        datastore.getAll()
            .test()
            .assertValue(
                categoryEntitiesWithAllSyncStatuses.map {
                    it.copy(syncStatus = SyncStatus.ToUpdate, name = updatedName)
                }
            )
    }

    @Test
    fun `Delete should set sync status to delete`() {
        val category = categoryEntity()
        datastore.add(category).blockingAwait()
        datastore.delete(category.id)
            .test()
            .assertComplete()

        datastore.getAll()
            .test()
            .assertValue(listOf(category.copy(syncStatus = SyncStatus.ToDelete)))
    }

    @Test
    fun `getCategoriesInWallet should return correct categories`() {
        val category = categoryEntity()
        val category2 = categoryEntity()

        datastore.updateWith(listOf(category, category2)).blockingAwait()

        datastore.getCategoriesInWallet(category.walletId)
            .test()
            .assertValue(listOf(category))
    }
}
