package com.aradipatrik.datasource.test

import com.aradipatrik.data.datastore.category.LocalCategoryDatastore
import com.aradipatrik.data.datastore.transaction.LocalTransactionDatastore
import com.aradipatrik.data.mapper.SyncStatus
import com.aradipatrik.data.mocks.DataLayerMocks.categoryEntity
import com.aradipatrik.data.mocks.DataLayerMocks.transactionWithIds
import com.aradipatrik.data.model.TransactionWithCategoryDataModel
import com.aradipatrik.local.database.RoomLocalCategoryDatastore
import com.aradipatrik.local.database.RoomLocalTransactionDatastore
import com.aradipatrik.local.database.mapper.CategoryRowMapper
import com.aradipatrik.local.database.mapper.TransactionRowMapper
import com.aradipatrik.testing.CommonMocks.hour
import com.aradipatrik.testing.CommonMocks.minute
import com.aradipatrik.testing.CommonMocks.string
import org.joda.time.DateTime
import org.joda.time.Interval
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.test.inject
import org.robolectric.RobolectricTestRunner
import java.util.*

@RunWith(RobolectricTestRunner::class)
class TransactionDatastoreTest : BaseRoomTest() {
    private val transactionDatastore: LocalTransactionDatastore by inject()
    private val categoryDatastore: LocalCategoryDatastore by inject()
    private val transactionEntitiesWithAllSyncStatus =
        EnumSet.allOf(SyncStatus::class.java).map { transactionWithIds(syncStatus = it) }

    @Test
    fun `Get in interval should return transactions in given interval, left right inclusive`() {
        val testYear = 2000
        val testMonth = 10
        val walletId = string()
        val testCategory = categoryEntity()
        val transactions =
            listOf(1, 2, 3, 4, 5).map { day ->
                transactionWithIds(
                    categoryId = testCategory.id,
                    syncStatus = SyncStatus.Synced,
                    date = DateTime(testYear, testMonth, day, hour(), minute()),
                    walletId = walletId
                )
            }

        categoryDatastore.updateWith(listOf(testCategory)).blockingAwait()
        transactionDatastore.updateWith(transactions).blockingAwait()

        val queryBeginIndex = 1
        val queryEndIndex = 3
        transactionDatastore.getInInterval(
            Interval(
                transactions[queryBeginIndex].date.toInstant(),
                transactions[queryEndIndex].date.toInstant()
            ),
            walletId = walletId
        )
            .test()
            .assertValue(
                transactions.subList(queryBeginIndex, queryEndIndex + 1).map {
                    TransactionWithCategoryDataModel(
                        id = it.id,
                        date = it.date,
                        category = testCategory,
                        syncStatus = it.syncStatus,
                        amount = it.amount,
                        memo = it.memo,
                        updatedTimeStamp = it.updatedTimeStamp,
                        walletId = it.walletId
                    )
                }
            )

    }

    @Test
    fun `Update with should insert the row representations of the entities passed`() {
        transactionDatastore.updateWith(transactionEntitiesWithAllSyncStatus)
            .test()
            .assertComplete()

        transactionDatastore.getAll()
            .test()
            .assertValue(transactionEntitiesWithAllSyncStatus)
    }

    @Test
    fun `Get pending should return those transactions which are not synced`() {
        transactionDatastore.updateWith(transactionEntitiesWithAllSyncStatus).blockingAwait()
        transactionDatastore.getPending()
            .test()
            .assertValue(
                transactionEntitiesWithAllSyncStatus.filter { it.syncStatus != SyncStatus.Synced }
            )
    }

    @Test
    fun `Clear pending should remove all transactions except those that are synced`() {
        transactionDatastore.updateWith(transactionEntitiesWithAllSyncStatus).blockingAwait()
        transactionDatastore.clearPending()
            .test()
            .assertComplete()

        transactionDatastore.getPending()
            .test()
            .assertValue(emptyList())

        transactionDatastore.getAll()
            .test()
            .assertValue(
                transactionEntitiesWithAllSyncStatus.filter { it.syncStatus == SyncStatus.Synced }
            )
    }

    @Test
    fun `Last sync time should return last sync time`() {
        val syncTimes = listOf<Long>(1, 2, 3, 4, 5)
        val syncedTransactions =
            syncTimes.map { transactionWithIds(updatedTimeStamp = it) }
        transactionDatastore.updateWith(syncedTransactions).blockingAwait()

        transactionDatastore.getLastSyncTime()
            .test()
            .assertValue(syncTimes.max())
    }

    @Test
    fun `Add should insert entity with to add sync status`() {
        transactionEntitiesWithAllSyncStatus.forEach {
            transactionDatastore.add(it)
                .test()
                .assertComplete()
        }

        transactionDatastore.getAll()
            .test()
            .assertValue(
                transactionEntitiesWithAllSyncStatus.map { it.copy(syncStatus = SyncStatus.ToAdd) }
            )
    }

    @Test
    fun `Update should update entity sync status with to update, and should update them`() {
        val updatedMemo = "updatedMemo"
        transactionDatastore.updateWith(transactionEntitiesWithAllSyncStatus).blockingAwait()
        transactionEntitiesWithAllSyncStatus.forEach {
            transactionDatastore.update(it.copy(memo = updatedMemo))
                .test()
                .assertComplete()
        }

        transactionDatastore.getAll()
            .test()
            .assertValue(
                transactionEntitiesWithAllSyncStatus.map {
                    it.copy(syncStatus = SyncStatus.ToUpdate, memo = updatedMemo)
                }
            )
    }

    @Test
    fun `Delete should set sync status to delete`() {
        val transaction = transactionWithIds()
        transactionDatastore.add(transaction).blockingAwait()
        transactionDatastore.delete(transaction.id)
            .test()
            .assertComplete()

        transactionDatastore.getAll()
            .test()
            .assertValue(listOf(transaction.copy(syncStatus = SyncStatus.ToDelete)))
    }
}
