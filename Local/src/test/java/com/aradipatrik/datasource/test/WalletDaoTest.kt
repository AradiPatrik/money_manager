package com.aradipatrik.datasource.test

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.aradipatrik.datasource.test.TransactionRowFactory.walletRow
import com.aradipatrik.local.database.TransactionDatabase
import com.aradipatrik.local.database.common.SyncStatusConstants.TO_ADD_CODE
import com.aradipatrik.local.database.common.SyncStatusConstants.TO_DELETE_CODE
import com.aradipatrik.local.database.common.SyncStatusConstants.TO_UPDATE_CODE
import com.aradipatrik.local.database.wallet.WalletRow
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class WalletDaoTest {
    @get:Rule
    val instantTaskExecutionRule = InstantTaskExecutorRule()

    private val database = Room.inMemoryDatabaseBuilder(
        ApplicationProvider.getApplicationContext(),
        TransactionDatabase::class.java
    )
        .allowMainThreadQueries()
        .build()

    private val pendingWalletInstances = listOf(
        walletRow(syncStatusCode = TO_DELETE_CODE),
        walletRow(syncStatusCode = TO_ADD_CODE),
        walletRow(syncStatusCode = TO_UPDATE_CODE)
    )

    private val testRow = walletRow()
    private val syncedRow = walletRow()
    private val dao get() = database.walletDao()
    private val rowToDelete = walletRow()
    private val randomRows = createNRandomWallets(2)

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun `Get all wallets returns data`() {
        initDbWithRows(testRow)
        val testObserver = dao.getAllWallets().test()
        testObserver.assertValue(listOf(testRow)).dispose()
    }

    @Test
    fun `Get pending wallets returns only pending data`() {
        initDbWithRows(pendingWalletInstances + syncedRow)
        val testObserver = dao.getPendingWallets().test()
        testObserver.assertValue(pendingWalletInstances)
    }

    @Test
    fun `Clear pending should clear all pending wallets`() {
        initDbWithRows(pendingWalletInstances + syncedRow)
        dao.clearPending().blockingAwait()
        assertOnlySyncedWalletRemains()
    }

    private fun assertOnlySyncedWalletRemains() {
        dao.getPendingWallets()
            .test()
            .assertValue(emptyList())
            .dispose()
        dao.getAllWallets()
            .test()
            .assertValue(listOf(syncedRow))
            .dispose()
    }

    @Test
    fun `Get last sync time should return last sync time`() {
        initDbWithRows(createRowsWithSyncTimeeStamps(1, 2, 3, 4, 5))
        assertLastSyncTimeIs(5)
    }

    private fun assertLastSyncTimeIs(lastSyncTime: Long) {
        dao.getLastSyncTime()
            .test()
            .assertValue(lastSyncTime)
            .dispose()
    }

    private fun createRowsWithSyncTimeeStamps(vararg syncTimes: Long) =
        syncTimes.map { walletRow(updateTimestamp = it) }

    @Test
    fun `Get last sync time should complete when no sync time is the db yet`() {
        dao.getLastSyncTime()
            .test()
            .assertNoValues()
            .assertComplete()
            .dispose()
    }

    @Test
    fun `Set deleted should set wallet sync code TO_DELETE`() {
        initDbWithRows(randomRows + rowToDelete)
        dao.setDeleted(rowToDelete.uid).blockingAwait()
        assertDeletedWalletBecamePendingWithDeletedSyncCode()
        assertGetAllWalletsContainsDeletedWallet()
    }

    private fun assertGetAllWalletsContainsDeletedWallet() {
        dao.getAllWallets()
            .test()
            .assertValue(
                randomRows + rowToDelete.copy(syncStatusCode = TO_DELETE_CODE)
            )
            .dispose()
    }

    private fun assertDeletedWalletBecamePendingWithDeletedSyncCode() {
        dao.getPendingWallets()
            .test()
            .assertValue(listOf(rowToDelete.copy(syncStatusCode = TO_DELETE_CODE)))
            .dispose()
    }

    private fun initDbWithRows(wallet: WalletRow) = initDbWithRows(listOf(wallet))

    private fun initDbWithRows(wallets: List<WalletRow>) = dao
        .insert(wallets)
        .blockingAwait()

    private fun createNRandomWallets(n: Int) = generateSequence { walletRow() }
        .take(n)
        .toList()
}