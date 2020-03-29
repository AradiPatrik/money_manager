package com.aradipatrik.datasource.test

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.aradipatrik.data.datastore.wallet.LocalWalletDatastore
import com.aradipatrik.data.mapper.SyncStatus
import com.aradipatrik.datasource.test.EntityFactory.walletEntity
import com.aradipatrik.local.database.RoomLocalWalletDatastore
import com.aradipatrik.local.database.TransactionDatabase
import com.aradipatrik.local.database.mapper.WalletRowMapper
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.*

@RunWith(RobolectricTestRunner::class)
class WalletDatastoreTest {
    @get:Rule
    val instantTaskExecutionRule = InstantTaskExecutorRule()

    private val database = Room.inMemoryDatabaseBuilder(
        ApplicationProvider.getApplicationContext(),
        TransactionDatabase::class.java
    )
        .allowMainThreadQueries()
        .build()

    private lateinit var datasource: LocalWalletDatastore
    private val walletRowMapper = WalletRowMapper()
    private val walletEntitiesWithAllSyncStatuses =
        EnumSet.allOf(SyncStatus::class.java).map { walletEntity(syncStatus = it) }

    @Before
    fun setup() {
        datasource = RoomLocalWalletDatastore(database.walletDao(), walletRowMapper)
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun `Update with should insert the row representations of the entities passed`() {
        datasource.updateWith(walletEntitiesWithAllSyncStatuses)
            .test()
            .assertComplete()

        datasource.getAll()
            .test()
            .assertValue(walletEntitiesWithAllSyncStatuses)
    }

    @Test
    fun `Get pending should return those wallets which are not synced`() {
        datasource.updateWith(walletEntitiesWithAllSyncStatuses).blockingAwait()
        datasource.getPending()
            .test()
            .assertValue(
                walletEntitiesWithAllSyncStatuses.filter { it.syncStatus != SyncStatus.Synced }
            )
    }

    @Test
    fun `Clear pending should remove all wallets except those that are synced`() {
        datasource.updateWith(walletEntitiesWithAllSyncStatuses).blockingAwait()
        datasource.clearPending()
            .test()
            .assertComplete()

        datasource.getPending()
            .test()
            .assertValue(emptyList())

        datasource.getAll()
            .test()
            .assertValue(
                walletEntitiesWithAllSyncStatuses.filter { it.syncStatus == SyncStatus.Synced }
            )
    }

    @Test
    fun `Last sync time should return last sync time`() {
        val syncTimes = listOf<Long>(1, 2, 3, 4, 5)
        val syncedWallets = syncTimes.map { walletEntity(updateTimestamp = it) }
        datasource.updateWith(syncedWallets).blockingAwait()

        datasource.getLastSyncTime()
            .test()
            .assertValue(syncTimes.max())
    }

    @Test
    fun `Add should insert entity with to add sync status`() {
        walletEntitiesWithAllSyncStatuses.forEach {
            datasource.add(it)
                .test()
                .assertComplete()
        }

        datasource.getAll()
            .test()
            .assertValue(
                walletEntitiesWithAllSyncStatuses.map { it.copy(syncStatus = SyncStatus.ToAdd) }
            )
    }

    @Test
    fun `Update should update entity sync status with to update, and should update them`() {
        val updatedName = "updatedName"
        datasource.updateWith(walletEntitiesWithAllSyncStatuses).blockingAwait()
        walletEntitiesWithAllSyncStatuses.forEach {
            datasource.update(it.copy(name = updatedName))
                .test()
                .assertComplete()
        }

        datasource.getAll()
            .test()
            .assertValue(
                walletEntitiesWithAllSyncStatuses.map {
                    it.copy(syncStatus = SyncStatus.ToUpdate, name = updatedName)
                }
            )
    }

    @Test
    fun `Delete should set sync status to delete`() {
        val entity = walletEntity()
        datasource.add(entity).blockingAwait()
        datasource.delete(entity.id)
            .test()
            .assertComplete()

        datasource.getAll()
            .test()
            .assertValue(listOf(entity.copy(syncStatus = SyncStatus.ToDelete)))
    }
}