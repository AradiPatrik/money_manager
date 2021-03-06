package com.aradipatrik.datasource.test

import com.aradipatrik.local.database.common.SyncStatusConstants.TO_ADD_CODE
import com.aradipatrik.local.database.common.SyncStatusConstants.TO_DELETE_CODE
import com.aradipatrik.local.database.common.SyncStatusConstants.TO_UPDATE_CODE
import com.aradipatrik.local.database.model.wallet.WalletDao
import com.aradipatrik.local.database.model.wallet.WalletRow
import com.aradipatrik.local.mocks.LocalMocks.walletRow
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.test.inject
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class WalletDaoTest : BaseRoomTest() {
    private val pendingWalletInstances = listOf(
        walletRow(syncStatusCode = TO_DELETE_CODE),
        walletRow(syncStatusCode = TO_ADD_CODE),
        walletRow(syncStatusCode = TO_UPDATE_CODE)
    )

    private val testRow = walletRow()
    private val syncedRow = walletRow()
    private val dao: WalletDao by inject()
    private val rowToDelete = walletRow()
    private val randomRows = createNRandomWallets(2)

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

    @Test
    fun `getWalletById should return the wallet with correct id`() {
        initDbWithRows(randomRows + testRow)
        dao.getWalletById(testRow.uid)
            .test()
            .assertValue(testRow)
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
