package com.aradipatrik.syncintegration

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.aradipatrik.data.mapper.CategoryMapper
import com.aradipatrik.data.mapper.SyncStatus
import com.aradipatrik.data.repository.CategoryRepositoryImpl
import com.aradipatrik.data.repository.Syncer
import com.aradipatrik.local.database.RoomLocalCategoryDatastore
import com.aradipatrik.local.database.RoomLocalTransactionDatastore
import com.aradipatrik.local.database.TransactionDatabase
import com.aradipatrik.local.database.mapper.CategoryRowMapper
import com.aradipatrik.local.database.mapper.TransactionRowMapper
import com.aradipatrik.remote.data.FirestoreRemoteCategoryDatastore
import com.aradipatrik.remote.data.FirestoreRemoteTransactionDatastore
import com.aradipatrik.remote.TEST_USER_ID
import com.aradipatrik.remote.payloadfactory.CategoryPayloadFactory
import com.aradipatrik.remote.payloadfactory.CategoryResponseConverter
import com.aradipatrik.remote.payloadfactory.TransactionPayloadFactory
import com.aradipatrik.remote.payloadfactory.TransactionResponseConverter
import com.aradipatrik.testing.DataLayerMocks.categoryEntity
import com.aradipatrik.testing.DomainLayerMocks.category
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import strikt.api.expectThat
import strikt.assertions.hasSize
import strikt.assertions.isEmpty
import strikt.assertions.isEqualTo

@RunWith(AndroidJUnit4::class)
class CategorySyncIntegration {

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

    private val categoryRepository = CategoryRepositoryImpl(
        syncer, categoryMapper, localCategoryDatastore
    )

    @After
    fun teardown() {
        database.close()
        remoteTransactionDatastore.deleteAllForTestUser()
        remoteCategoryDatastore.deleteAllForTestUser()
    }

    @Test
    fun afterAddThereShouldBeNoMorePendingCategoriesLeft() {
        categoryRepository.add(category()).blockingAwait()

        val pendingCategories = localCategoryDatastore.getPending().blockingGet()

        expectThat(pendingCategories).isEmpty()
    }

    @Test
    fun afterAddThereShouldBeOneEntityInLocalAndRemoteDatabases() {
        categoryRepository.add(category()).blockingAwait()

        val allLocal = localCategoryDatastore.getAll().test().awaitCount(1).values().first()
        expectThat(allLocal).hasSize(1)
        val allRemote = remoteCategoryDatastore.getAfter(0).blockingGet()
        expectThat(allRemote).hasSize(1)
    }

    @Test
    fun afterAddTheRemoteAndTheLocalEntityShouldBeTheSame() {
        val originalCategory = category()
        categoryRepository.add(originalCategory).blockingAwait()

        val localResult =
            localCategoryDatastore.getAll().test().awaitCount(1).values().first().first()
        val remoteResult = remoteCategoryDatastore.getAfter(0).blockingGet().first()

        expectThat(localResult).isEqualTo(remoteResult)
    }

    @Test
    fun ifOtherDeviceAddedACategoryItShouldBeReflectedInLocalAfterRefresh() {
        val localCategory = category(name = "original")
        val otherDevicesCategory = category(name = "remote")
        categoryRepository.add(localCategory).blockingAwait()
        val testObserver = categoryRepository.getAll().test()
        val afterLocalAdd = testObserver.awaitCount(1).values().first()
        expectThat(afterLocalAdd).hasSize(1)

        remoteCategoryDatastore.updateWith(
            listOf(
                categoryMapper.mapToEntity(otherDevicesCategory).copy(
                    syncStatus = SyncStatus.ToAdd
                )
            )
        ).blockingAwait()

        val afterRemoteChangeAndSync = categoryRepository.getAll().test().awaitCount(1).values().first()
        val lastValue = testObserver.values().last()
        expectThat(afterRemoteChangeAndSync).hasSize(2)
        expectThat(lastValue).hasSize(2)
    }

    @Test
    fun ifOtherDeviceUpdatedACategoryItShouldBeReflectedInLocalAfterRefresh() {
        val localCategory = category(name = "original")
        categoryRepository.add(localCategory).blockingAwait()
        val testObserver = categoryRepository.getAll().test()
        val afterLocalAdd = testObserver.awaitCount(1).values().first()
        expectThat(afterLocalAdd).hasSize(1)
        val originalId = afterLocalAdd.first().id

        remoteCategoryDatastore.updateWith(
            listOf(
                categoryEntity(id = originalId, name = "updated", syncStatus = SyncStatus.ToUpdate)
            )
        ).blockingAwait()

        val afterRemoteChangeAndSync = categoryRepository.getAll().test().awaitCount(1).values().first()
        val lastValue = testObserver.values().last()
        expectThat(afterRemoteChangeAndSync).hasSize(1)
        expectThat(lastValue).hasSize(1)

        expectThat(lastValue.first().name).isEqualTo("updated")
    }

    @Test
    fun ifOtherDeviceDeletedAnItemItShouldBeReflectedInLocalAfterRefresh() {
        val localCategory = category(name = "original")
        categoryRepository.add(localCategory).blockingAwait()
        val testObserver = categoryRepository.getAll().test()
        val afterLocalAdd = testObserver.awaitCount(1).values().first()
        expectThat(afterLocalAdd).hasSize(1)
        val originalId = afterLocalAdd.first().id

        remoteCategoryDatastore.updateWith(
            listOf(
                categoryEntity(id = originalId, syncStatus = SyncStatus.ToDelete)
            )
        ).blockingAwait()

        val afterRemoteChangeAndSync = categoryRepository.getAll().test().awaitCount(1).values().first()
        val lastValue = testObserver.values().last()
        expectThat(afterRemoteChangeAndSync).hasSize(1)
        expectThat(lastValue).hasSize(1)

        val category = localCategoryDatastore.getPending().test().values().last().first()
        expectThat(category.syncStatus).isEqualTo(SyncStatus.ToDelete)
    }
}
