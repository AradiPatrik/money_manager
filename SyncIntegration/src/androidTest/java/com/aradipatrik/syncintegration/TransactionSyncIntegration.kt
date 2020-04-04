package com.aradipatrik.syncintegration

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.aradipatrik.data.mapper.CategoryMapper
import com.aradipatrik.data.mapper.JoinedTransactionMapper
import com.aradipatrik.data.mapper.PartialTransactionMapper
import com.aradipatrik.data.mapper.SyncStatus
import com.aradipatrik.data.repository.CategoryRepositoryImpl
import com.aradipatrik.data.repository.Syncer
import com.aradipatrik.data.repository.TransactionRepositoryImpl
import com.aradipatrik.domain.model.Category
import com.aradipatrik.local.database.RoomLocalCategoryDatastore
import com.aradipatrik.local.database.RoomLocalTransactionDatastore
import com.aradipatrik.local.database.TransactionDatabase
import com.aradipatrik.local.database.mapper.CategoryRowMapper
import com.aradipatrik.local.database.mapper.TransactionRowMapper
import com.aradipatrik.remote.TEST_USER_ID
import com.aradipatrik.remote.data.FirestoreRemoteCategoryDatastore
import com.aradipatrik.remote.data.FirestoreRemoteTransactionDatastore
import com.aradipatrik.remote.payloadfactory.CategoryPayloadFactory
import com.aradipatrik.remote.payloadfactory.CategoryResponseConverter
import com.aradipatrik.remote.payloadfactory.TransactionPayloadFactory
import com.aradipatrik.remote.payloadfactory.TransactionResponseConverter
import com.aradipatrik.testing.DomainLayerMocks.category
import com.aradipatrik.testing.DomainLayerMocks.transaction
import org.joda.time.DateTime
import org.joda.time.YearMonth
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import strikt.api.expectThat
import strikt.assertions.hasSize
import strikt.assertions.isEmpty
import strikt.assertions.isEqualTo

@RunWith(AndroidJUnit4::class)
class TransactionSyncIntegration {
    @get:Rule
    val instantTaskExecutionRule = InstantTaskExecutorRule()

    private val categoryPayloadFactory = CategoryPayloadFactory()
    private val categoryResponseConverter = CategoryResponseConverter()

    private val transactionPayloadFactory = TransactionPayloadFactory()
    private val transactionResponseConverter = TransactionResponseConverter()

    private val remoteTransactionDatastore =
        FirestoreRemoteTransactionDatastore(
            TEST_USER_ID, transactionPayloadFactory, transactionResponseConverter
        )

    private val remoteCategoryDatastore =
        FirestoreRemoteCategoryDatastore(
            TEST_USER_ID, categoryPayloadFactory, categoryResponseConverter
        )

    private val database = Room.inMemoryDatabaseBuilder(
        InstrumentationRegistry.getInstrumentation().context,
        TransactionDatabase::class.java
    )
        .allowMainThreadQueries()
        .build()

    private val categoryRowMapper = CategoryRowMapper()
    private val transactionRowMapper = TransactionRowMapper(categoryRowMapper)

    private val localTransactionDatastore = RoomLocalTransactionDatastore(
        database.transactionDao(), transactionRowMapper
    )

    private val localCategoryDatastore = RoomLocalCategoryDatastore(
        database.categoryDao(), categoryRowMapper
    )

    private val syncer = Syncer(
        remoteTransactionDatastore, localTransactionDatastore,
        remoteCategoryDatastore, localCategoryDatastore
    )

    private val categoryMapper = CategoryMapper()
    private val partialTransactionMapper = PartialTransactionMapper()
    private val joinedTransactionMapper = JoinedTransactionMapper(categoryMapper)

    private val categoryRepository = CategoryRepositoryImpl(
        syncer, categoryMapper, localCategoryDatastore
    )
    private val transactionRepository = TransactionRepositoryImpl(
        syncer, partialTransactionMapper, joinedTransactionMapper, localTransactionDatastore
    )

    private lateinit var defaultCategories: List<Category>

    @Before
    fun setup() {
        repeat(2) { categoryRepository.add(category()).blockingAwait() }
        defaultCategories = categoryRepository.getAll().blockingFirst()
    }

    @After
    fun teardown() {
        database.close()
        remoteTransactionDatastore.deleteAllForTestUser()
        remoteCategoryDatastore.deleteAllForTestUser()
    }

    @Test
    fun afterAddThereShouldBeNoMorePendingTransactionsLeft() {
        val transactionToAdd = transaction(date = DateTime.now(), category = defaultCategories[0])
        transactionRepository.add(transactionToAdd).blockingAwait()

        val pendingTransactions = localTransactionDatastore.getPending().blockingGet()

        expectThat(pendingTransactions).isEmpty()
    }

    @Test
    fun afterAddThereShouldBeOneEntityInLocalAndRemoteDatabases() {
        transactionRepository.add(transaction(category = defaultCategories[0])).blockingAwait()

        val allLocal = localTransactionDatastore.getAll().test().awaitCount(1).values().first()
        expectThat(allLocal).hasSize(1)
        val allRemote = remoteTransactionDatastore.getAfter(0).blockingGet()
        expectThat(allRemote).hasSize(1)
    }

    @Test
    fun afterAddTheRemoteAndTheLocalEntityShouldBeTheSame() {
        val originalTransaction = transaction(category = defaultCategories[0])
        transactionRepository.add(originalTransaction).blockingAwait()

        val localResult =
            localTransactionDatastore.getAll().test().awaitCount(1).values().first().first()
        val remoteResult = remoteTransactionDatastore.getAfter(0).blockingGet().first()

        expectThat(localResult).isEqualTo(remoteResult)
    }

    @Test
    fun ifOtherDeviceAddedATransactionItShouldBeReflectedInLocalAfterRefresh() {
        val localTransaction = transaction(
            memo = "original",
            date = DateTime.now(),
            category = defaultCategories[0]
        )
        val otherDevicesTransaction = transaction(
            memo = "remote",
            date = DateTime.now(),
            category = defaultCategories[1]
        )
        transactionRepository.add(localTransaction).blockingAwait()
        val testObserver = transactionRepository.getInInterval(YearMonth.now().toInterval()).test()
        val afterLocalAdd = testObserver.awaitCount(1).values().first()
        expectThat(afterLocalAdd).hasSize(1)

        remoteTransactionDatastore.updateWith(
            listOf(
                partialTransactionMapper.mapToEntity(otherDevicesTransaction).copy(
                    syncStatus = SyncStatus.ToAdd
                )
            )
        ).blockingAwait()

        val afterRemoteChangeAndSync = transactionRepository
            .getInInterval(YearMonth.now().toInterval())
            .test()
            .awaitCount(1)
            .values()
            .first()

        val lastValue = testObserver.values().last()
        expectThat(afterRemoteChangeAndSync).hasSize(2)
        expectThat(lastValue).hasSize(2)
    }

    @Test
    fun ifOtherDeviceUpdatedACategoryItShouldBeReflectedInLocalAfterRefresh() {
        val localTransaction = transaction(
            memo = "original",
            date = DateTime.now(),
            category = defaultCategories[0]
        )
        transactionRepository.add(localTransaction).blockingAwait()
        val testObserver = transactionRepository.getInInterval(YearMonth.now().toInterval()).test()
        val afterLocalAdd = testObserver.awaitCount(1).values().first()
        expectThat(afterLocalAdd).hasSize(1)

        val originalId = afterLocalAdd.first().id

        remoteTransactionDatastore.updateWith(
            listOf(
                partialTransactionMapper.mapToEntity(localTransaction)
                    .copy(id = originalId, memo = "updated", syncStatus = SyncStatus.ToUpdate)
            )
        ).blockingAwait()

        val afterRemoteChangeAndSync = transactionRepository
            .getInInterval(YearMonth.now().toInterval())
            .test()
            .awaitCount(1)
            .values()
            .first()

        val lastValue = testObserver.values().last()
        expectThat(afterRemoteChangeAndSync).hasSize(1)
        expectThat(lastValue).hasSize(1)

        expectThat(lastValue.first().memo).isEqualTo("updated")
    }

    @Test
    fun ifOtherDeviceDeletedAnItemItShouldBeReflectedInLocalAfterRefresh() {
        val localTransaction = transaction(
            memo = "original",
            date = DateTime.now(),
            category = defaultCategories[0]
        )

        transactionRepository.add(localTransaction).blockingAwait()
        val testObserver = transactionRepository.getInInterval(YearMonth.now().toInterval()).test()
        val afterLocalAdd = testObserver.awaitCount(1).values().first()
        expectThat(afterLocalAdd).hasSize(1)
        val originalId = afterLocalAdd.first().id

        remoteTransactionDatastore.updateWith(
            listOf(
                partialTransactionMapper.mapToEntity(localTransaction)
                    .copy(id = originalId, syncStatus = SyncStatus.ToDelete)
            )
        ).blockingAwait()

        val afterRemoteChangeAndSync = transactionRepository
            .getInInterval(YearMonth.now().toInterval())
            .test()
            .awaitCount(1)
            .values()
            .first()

        val lastValue = testObserver.values().last()
        expectThat(afterRemoteChangeAndSync).hasSize(0)
        expectThat(lastValue).hasSize(0)

        val transaction = localTransactionDatastore.getPending().test().values().last().first()
        expectThat(transaction.syncStatus).isEqualTo(SyncStatus.ToDelete)
    }

    @Test
    fun getInIntervalShouldJoinTransactionAndCategoryCorrectly() {
        val transactionToAdd = transaction(category = defaultCategories[0], date = DateTime.now())
        transactionRepository.add(transactionToAdd).blockingAwait()

        val joinedTransactions =
            transactionRepository.getInInterval(YearMonth.now().toInterval()).blockingFirst()

        expectThat(joinedTransactions).hasSize(1)
        expectThat(joinedTransactions.first().category).isEqualTo(defaultCategories[0])
    }

    @Test
    fun shouldBeAbleToSyncIndependentOfRepositories() {
        val transactionA = transaction(
            memo = "original",
            date = DateTime.now(),
            category = defaultCategories[0]
        )

        remoteTransactionDatastore.updateWith(
            listOf(
                partialTransactionMapper.mapToEntity(transactionA).copy(
                    syncStatus = SyncStatus.ToAdd
                )
            )
        ).blockingAwait()

        syncer.syncAll().blockingAwait()

        val localTransactions = localTransactionDatastore.getAll().blockingFirst()
        expectThat(localTransactions.size).isEqualTo(1)
    }
}
