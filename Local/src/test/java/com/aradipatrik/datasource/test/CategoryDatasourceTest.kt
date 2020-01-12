package com.aradipatrik.datasource.test

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import com.aradipatrik.data.datasource.category.LocalCategoryDataStore
import com.aradipatrik.data.mapper.SyncStatus
import com.aradipatrik.datasource.test.TransactionRowFactory.categoryRow
import com.aradipatrik.local.database.RoomLocalCategoryDataSource
import com.aradipatrik.local.database.TransactionDatabase
import com.aradipatrik.local.database.mapper.CategoryRowMapper
import com.aradipatrik.testing.DataLayerMocks.categoryEntity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import java.util.*

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class CategoryDatasourceTest {
    @get:Rule
    val instantTaskExecutionRule = InstantTaskExecutorRule()

    private val database = Room.inMemoryDatabaseBuilder(
        RuntimeEnvironment.application.applicationContext,
        TransactionDatabase::class.java
    )
        .allowMainThreadQueries()
        .build()

    private lateinit var datasource: LocalCategoryDataStore
    private val categoryRowMapper = CategoryRowMapper()
    private val categoryEntitiesWithAllSyncStatuses =
        EnumSet.allOf(SyncStatus::class.java).map { categoryEntity(syncStatus = it) }

    @Before
    fun setup() {
        datasource = RoomLocalCategoryDataSource(database.categoryDao(), categoryRowMapper)
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun `Update with should insert the row representations of the entities passed`() {
        datasource.updateWith(categoryEntitiesWithAllSyncStatuses)
            .test()
            .assertComplete()

        datasource.getAll()
            .test()
            .assertValue(categoryEntitiesWithAllSyncStatuses)
    }

    @Test
    fun `Get pending should return those categories which are not synced`() {
        datasource.updateWith(categoryEntitiesWithAllSyncStatuses).blockingAwait()
        datasource.getPending()
            .test()
            .assertValue(
                categoryEntitiesWithAllSyncStatuses.filter { it.syncStatus != SyncStatus.Synced }
            )
    }

    @Test
    fun `Clear pending should remove all categories except those that are synced`() {
        datasource.updateWith(categoryEntitiesWithAllSyncStatuses).blockingAwait()
        datasource.clearPending()
            .test()
            .assertComplete()

        datasource.getPending()
            .test()
            .assertValue(emptyList())

        datasource.getAll()
            .test()
            .assertValue(
                categoryEntitiesWithAllSyncStatuses.filter { it.syncStatus == SyncStatus.Synced }
            )
    }

    @Test
    fun `Last sync time should return last sync time`() {
        val syncTimes = listOf<Long>(1, 2, 3, 4, 5)
        val syncedCategories = syncTimes.map { categoryEntity(lastUpdateTimestamp = it) }
        datasource.updateWith(syncedCategories).blockingAwait()

        datasource.getLastSyncTime()
            .test()
            .assertValue(syncTimes.max())
    }

    @Test
    fun `Add should insert entity with to add sync status`() {
        categoryEntitiesWithAllSyncStatuses.forEach {
            datasource.add(it)
                .test()
                .assertComplete()
        }

        datasource.getAll()
            .test()
            .assertValue(
                categoryEntitiesWithAllSyncStatuses.map { it.copy(syncStatus = SyncStatus.ToAdd) }
            )
    }

    @Test
    fun `Update should update entity sync status with to update, and should update them`() {
        val updatedName = "updatedName"
        datasource.updateWith(categoryEntitiesWithAllSyncStatuses).blockingAwait()
        categoryEntitiesWithAllSyncStatuses.forEach {
            datasource.update(it.copy(name = updatedName))
                .test()
                .assertComplete()
        }

        datasource.getAll()
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
        datasource.add(category).blockingAwait()
        datasource.delete(category.id)
            .test()
            .assertComplete()

        datasource.getAll()
            .test()
            .assertValue(listOf(category.copy(syncStatus = SyncStatus.ToDelete)))
    }
}