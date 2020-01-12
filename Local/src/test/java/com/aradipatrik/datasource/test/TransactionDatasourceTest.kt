package com.aradipatrik.datasource.test

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import com.aradipatrik.data.datasource.category.LocalCategoryDataStore
import com.aradipatrik.data.datasource.transaction.LocalTransactionDataStore
import com.aradipatrik.data.mapper.SyncStatus
import com.aradipatrik.data.model.TransactionJoinedEntity
import com.aradipatrik.local.database.RoomLocalCategoryDataSource
import com.aradipatrik.local.database.RoomLocalTransactionDataSource
import com.aradipatrik.local.database.TransactionDatabase
import com.aradipatrik.local.database.mapper.CategoryRowMapper
import com.aradipatrik.local.database.mapper.TransactionRowMapper
import com.aradipatrik.testing.DataLayerMocks.categoryEntity
import com.aradipatrik.testing.DataLayerMocks.partialTransactionEntity
import com.aradipatrik.testing.DomainLayerMocks
import com.aradipatrik.testing.DomainLayerMocks.category
import com.aradipatrik.testing.DomainLayerMocks.hour
import com.aradipatrik.testing.DomainLayerMocks.minute
import org.joda.time.DateTime
import org.joda.time.Interval
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
class TransactionDatasourceTest {
    @get:Rule
    val instantTaskExecutionRule = InstantTaskExecutorRule()

    private val database = Room.inMemoryDatabaseBuilder(
        RuntimeEnvironment.application.applicationContext,
        TransactionDatabase::class.java
    )
        .allowMainThreadQueries()
        .build()

    private lateinit var transactionDataStore: LocalTransactionDataStore
    private lateinit var categoryDataStore: LocalCategoryDataStore
    private val categoryRowMapper = CategoryRowMapper()
    private val transactionRowMapper = TransactionRowMapper(categoryRowMapper)
    private val transactionEntitiesWithAllSyncStatus =
        EnumSet.allOf(SyncStatus::class.java).map { partialTransactionEntity(syncStatus = it) }

    @Before
    fun setup() {
        transactionDataStore =
            RoomLocalTransactionDataSource(database.transactionDao(), transactionRowMapper)
        categoryDataStore = RoomLocalCategoryDataSource(database.categoryDao(), categoryRowMapper)
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun `Get in interval should return transactions in given interval, left right inclusive`() {
        val testYear = 2000
        val testMonth = 10
        val testCategory = categoryEntity()
        val transactions =
            listOf(1, 2, 3, 4, 5).map { day ->
                partialTransactionEntity(
                    categoryId = testCategory.id,
                    syncStatus = SyncStatus.Synced,
                    date = DateTime(testYear, testMonth, day, hour(), minute())
                )
            }

        categoryDataStore.updateWith(listOf(testCategory)).blockingAwait()
        transactionDataStore.updateWith(transactions).blockingAwait()

        val queryBeginIndex = 1
        val queryEndIndex = 3
        transactionDataStore.getInInterval(
            Interval(
                transactions[queryBeginIndex].date.toInstant(),
                transactions[queryEndIndex].date.toInstant()
            )
        )
            .test()
            .assertValue(
                transactions.subList(queryBeginIndex, queryEndIndex + 1).map {
                    TransactionJoinedEntity(
                        id = it.id,
                        date = it.date,
                        category = testCategory,
                        syncStatus = it.syncStatus,
                        amount = it.amount,
                        memo = it.memo,
                        updatedTimeStamp = it.updatedTimeStamp
                    )
                }
            )

    }

    @Test
    fun `Update with should insert the row representations of the entities passed`() {
        transactionDataStore.updateWith(transactionEntitiesWithAllSyncStatus)
            .test()
            .assertComplete()

        transactionDataStore.getAll()
            .test()
            .assertValue(transactionEntitiesWithAllSyncStatus)
    }

    @Test
    fun `Get pending should return those transactions which are not synced`() {
        transactionDataStore.updateWith(transactionEntitiesWithAllSyncStatus).blockingAwait()
        transactionDataStore.getPending()
            .test()
            .assertValue(
                transactionEntitiesWithAllSyncStatus.filter { it.syncStatus != SyncStatus.Synced }
            )
    }

    @Test
    fun `Clear pending should remove all transactions except those that are synced`() {
        transactionDataStore.updateWith(transactionEntitiesWithAllSyncStatus).blockingAwait()
        transactionDataStore.clearPending()
            .test()
            .assertComplete()

        transactionDataStore.getPending()
            .test()
            .assertValue(emptyList())

        transactionDataStore.getAll()
            .test()
            .assertValue(
                transactionEntitiesWithAllSyncStatus.filter { it.syncStatus == SyncStatus.Synced }
            )
    }

    @Test
    fun `Last sync time should return last sync time`() {
        val syncTimes = listOf<Long>(1, 2, 3, 4, 5)
        val syncedTransactions =
            syncTimes.map { partialTransactionEntity(lastUpdateTimestamp = it) }
        transactionDataStore.updateWith(syncedTransactions).blockingAwait()

        transactionDataStore.getLastSyncTime()
            .test()
            .assertValue(syncTimes.max())
    }

    @Test
    fun `Add should insert entity with to add sync status`() {
        transactionEntitiesWithAllSyncStatus.forEach {
            transactionDataStore.add(it)
                .test()
                .assertComplete()
        }

        transactionDataStore.getAll()
            .test()
            .assertValue(
                transactionEntitiesWithAllSyncStatus.map { it.copy(syncStatus = SyncStatus.ToAdd) }
            )
    }

    @Test
    fun `Update should update entity sync status with to update, and should update them`() {
        val updatedMemo = "updatedMemo"
        transactionDataStore.updateWith(transactionEntitiesWithAllSyncStatus).blockingAwait()
        transactionEntitiesWithAllSyncStatus.forEach {
            transactionDataStore.update(it.copy(memo = updatedMemo))
                .test()
                .assertComplete()
        }

        transactionDataStore.getAll()
            .test()
            .assertValue(
                transactionEntitiesWithAllSyncStatus.map {
                    it.copy(syncStatus = SyncStatus.ToUpdate, memo = updatedMemo)
                }
            )
    }

    @Test
    fun `Delete should set sync status to delete`() {
        val transaction = partialTransactionEntity()
        transactionDataStore.add(transaction).blockingAwait()
        transactionDataStore.delete(transaction.id)
            .test()
            .assertComplete()

        transactionDataStore.getAll()
            .test()
            .assertValue(listOf(transaction.copy(syncStatus = SyncStatus.ToDelete)))
    }
}
