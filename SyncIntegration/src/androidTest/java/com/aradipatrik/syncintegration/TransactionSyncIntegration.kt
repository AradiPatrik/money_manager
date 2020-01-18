package com.aradipatrik.syncintegration

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.aradipatrik.data.mapper.CategoryMapper
import com.aradipatrik.data.mapper.SyncStatus
import com.aradipatrik.data.repository.CategoryRepositoryImpl
import com.aradipatrik.data.repository.Syncer
import com.aradipatrik.data.repository.TransactionRepositoryImpl
import com.aradipatrik.local.database.RoomLocalCategoryDataSource
import com.aradipatrik.local.database.RoomLocalTransactionDataSource
import com.aradipatrik.local.database.TransactionDatabase
import com.aradipatrik.local.database.mapper.CategoryRowMapper
import com.aradipatrik.local.database.mapper.TransactionRowMapper
import com.aradipatrik.remote.FirestoreRemoteCategoryDatastore
import com.aradipatrik.remote.FirestoreRemoteTransactionDatastore
import com.aradipatrik.remote.TEST_USER_ID
import com.aradipatrik.remote.payloadfactory.CategoryPayloadFactory
import com.aradipatrik.remote.payloadfactory.CategoryResponseConverter
import com.aradipatrik.remote.payloadfactory.TransactionPayloadFactory
import com.aradipatrik.remote.payloadfactory.TransactionResponseConverter
import com.aradipatrik.testing.DataLayerMocks
import com.aradipatrik.testing.DomainLayerMocks
import org.junit.After
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

    private val remoteTransactionDatastore = FirestoreRemoteTransactionDatastore(
        TEST_USER_ID, transactionPayloadFactory, transactionResponseConverter
    )

    private val remoteCategoryDatastore = FirestoreRemoteCategoryDatastore(
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

    private val localTransactionDatastore = RoomLocalTransactionDataSource(
        database.transactionDao(), transactionRowMapper
    )

    private val localCategoryDatastore = RoomLocalCategoryDataSource(
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
}