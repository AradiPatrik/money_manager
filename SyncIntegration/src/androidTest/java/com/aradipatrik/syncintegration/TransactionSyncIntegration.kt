package com.aradipatrik.syncintegration

import android.preference.PreferenceManager
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.aradipatrik.data.dataModule
import com.aradipatrik.data.datastore.category.LocalCategoryDatastore
import com.aradipatrik.data.datastore.category.RemoteCategoryDatastore
import com.aradipatrik.data.datastore.transaction.LocalTransactionDatastore
import com.aradipatrik.data.datastore.transaction.RemoteTransactionDatastore
import com.aradipatrik.data.datastore.wallet.RemoteWalletDatastore
import com.aradipatrik.data.mapper.CategoryMapper
import com.aradipatrik.data.mapper.JoinedTransactionMapper
import com.aradipatrik.data.mapper.PartialTransactionMapper
import com.aradipatrik.data.mapper.SyncStatus
import com.aradipatrik.data.mapper.WalletMapper
import com.aradipatrik.data.mocks.DataLayerMocks
import com.aradipatrik.data.mocks.DataLayerMocks.walletDataModel
import com.aradipatrik.data.repository.CategoryRepositoryImpl
import com.aradipatrik.data.repository.Syncer
import com.aradipatrik.data.repository.TransactionRepositoryImpl
import com.aradipatrik.domain.interfaces.auth.Authenticator
import com.aradipatrik.domain.interfaces.data.CategoryRepository
import com.aradipatrik.domain.interfaces.data.TransactionRepository
import com.aradipatrik.domain.interfaces.data.UserRepository
import com.aradipatrik.domain.interfaces.data.WalletRepository
import com.aradipatrik.domain.mocks.DomainLayerMocks.category
import com.aradipatrik.domain.mocks.DomainLayerMocks.transaction
import com.aradipatrik.domain.model.Category
import com.aradipatrik.domain.model.User
import com.aradipatrik.domain.model.UserCredentials
import com.aradipatrik.integration.firebase.utils.FirestoreUtils
import com.aradipatrik.local.database.RoomLocalCategoryDatastore
import com.aradipatrik.local.database.RoomLocalTransactionDatastore
import com.aradipatrik.local.database.TransactionDatabase
import com.aradipatrik.local.database.localModule
import com.aradipatrik.local.database.mapper.CategoryRowMapper
import com.aradipatrik.local.database.mapper.TransactionRowMapper
import com.aradipatrik.remote.TEST_USER_ID
import com.aradipatrik.remote.data.FirestoreRemoteCategoryDatastore
import com.aradipatrik.remote.data.FirestoreRemoteTransactionDatastore
import com.aradipatrik.remote.payloadfactory.CategoryPayloadFactory
import com.aradipatrik.remote.payloadfactory.CategoryResponseConverter
import com.aradipatrik.remote.payloadfactory.TransactionPayloadFactory
import com.aradipatrik.remote.payloadfactory.TransactionResponseConverter
import com.aradipatrik.remote.remoteModule
import com.f2prateek.rx.preferences2.RxSharedPreferences
import com.google.firebase.FirebaseApp
import org.joda.time.DateTime
import org.joda.time.YearMonth
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Categories
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import strikt.api.expectThat
import strikt.assertions.hasSize
import strikt.assertions.isEmpty
import strikt.assertions.isEqualTo

@RunWith(AndroidJUnit4::class)
class TransactionSyncIntegration : KoinTest {
    @get:Rule
    val instantTaskExecutionRule = InstantTaskExecutorRule()

    private val testOverrideModule = module {
        single(override = true) {
            Room.inMemoryDatabaseBuilder(
                InstrumentationRegistry.getInstrumentation().context,
                TransactionDatabase::class.java
            )
                .allowMainThreadQueries()
                .build()
        }
        single(override = true) {
            RxSharedPreferences.create(
                PreferenceManager.getDefaultSharedPreferences(
                    InstrumentationRegistry.getInstrumentation().context
                )
            )
        }
    }

    private val remoteCategoryDatastore: RemoteCategoryDatastore by inject()

    private val database = Room.inMemoryDatabaseBuilder(
        InstrumentationRegistry.getInstrumentation().context,
        TransactionDatabase::class.java
    )
        .allowMainThreadQueries()
        .build()

    private val remoteWalletDatastore: RemoteWalletDatastore by inject()
    private val categoryRepository: CategoryRepository by inject()
    private val authenticator: Authenticator by inject()
    private val userRepository: UserRepository by inject()
    private val transactionRepository: TransactionRepository by inject()
    private val localTransactionDatastore: LocalTransactionDatastore by inject()
    private val remoteTransactionDatastore: RemoteTransactionDatastore by inject()
    private val partialTransactionMapper: PartialTransactionMapper by inject()
    private val syncer: Syncer by inject()
    private val walletRepository: WalletRepository by inject()
    private val walletMapper: WalletMapper by inject()

    private lateinit var userId: String
    private lateinit var defaultCategories: List<Category>
    private var walletA = walletDataModel(name = "walletA", syncStatus = SyncStatus.ToAdd)
    private var walletB = walletDataModel(name = "walletB", syncStatus = SyncStatus.ToAdd)

    @Before
    fun setup() {
        setupKoin()
        FirebaseApp.initializeApp(InstrumentationRegistry.getInstrumentation().context)
        userId = authenticator.registerUserWithCredentials(
            UserCredentials("testemail@gmail.com", "Almafa123")
        ).blockingGet().id
        remoteWalletDatastore.updateWith(listOf(walletA, walletB), userId).blockingAwait()
        userRepository.setSignedInUser(User(userId)).blockingAwait()
        val list = remoteWalletDatastore.getAfter(0, userId).blockingGet()
        walletA = walletA.copy(id = list.first { it.name == walletA.name }.id)
        walletB = walletB.copy(id = list.first { it.name == walletB.name }.id)
        repeat(2) { categoryRepository.add(category(walletId = walletA.id)).blockingAwait() }
        defaultCategories = categoryRepository.getAll().blockingFirst()
        walletRepository.setSelectedWallet(walletMapper.mapFromEntity(walletA)).blockingAwait()
    }

    private fun setupKoin() {
        startKoin {
            modules(
                listOf(
                    remoteModule,
                    localModule,
                    dataModule,
                    testOverrideModule
                )
            )
        }
    }

    @After
    fun teardown() {
        database.close()
        (remoteCategoryDatastore as FirestoreRemoteCategoryDatastore)
            .blockingCleanupUserCategories(userId)
        FirestoreUtils.deleteWalletsOfUser(userId)
        FirestoreUtils.deleteUserById(userId)
        FirestoreUtils.removeAuthenticatedUser()
        stopKoin()
    }

    @Test
    fun afterAddThereShouldBeNoMorePendingTransactionsLeft() {
        val transactionToAdd = transaction(category = defaultCategories[0], walletId = walletA.id)
        transactionRepository.add(transactionToAdd).blockingAwait()

        val pendingTransactions = localTransactionDatastore.getPending().blockingGet()

        expectThat(pendingTransactions).isEmpty()
    }

    @Test
    fun afterAddThereShouldBeOneEntityInLocalAndRemoteDatabases() {
        transactionRepository.add(
            transaction(
                category = defaultCategories[0],
                walletId = walletA.id
            )
        ).blockingAwait()

        val allLocal = localTransactionDatastore.getAll()
            .test()
            .awaitCount(1)
            .values()
            .first()

        expectThat(allLocal).hasSize(1)
        val allRemote = remoteTransactionDatastore.getAfter(0, userId).blockingGet()
        expectThat(allRemote).hasSize(1)
    }

    @Test
    fun afterAddTheRemoteAndTheLocalEntityShouldBeTheSame() {
        val originalTransaction = transaction(
            category = defaultCategories[0],
            walletId = walletA.id
        )
        transactionRepository.add(originalTransaction).blockingAwait()

        val localResult =
            localTransactionDatastore.getAll().test().awaitCount(1).values().first().first()
        val remoteResult = remoteTransactionDatastore.getAfter(0, userId).blockingGet().first()

        expectThat(localResult).isEqualTo(remoteResult)
    }

    @Test
    fun ifOtherDeviceAddedATransactionItShouldBeReflectedInLocalAfterRefresh() {
        val localTransaction = transaction(
            memo = "original",
            date = DateTime.now(),
            category = defaultCategories[0],
            walletId = walletA.id
        )
        val otherDevicesTransaction = transaction(
            memo = "remote",
            date = DateTime.now(),
            category = defaultCategories[1],
            walletId = walletA.id
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
            ),
            userId
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
    fun ifOtherDeviceUpdatedATransactionItShouldBeReflectedInLocalAfterRefresh() {
        val localTransaction = transaction(
            memo = "original",
            date = DateTime.now(),
            category = defaultCategories[0],
            walletId = walletA.id
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
            ),
            userId
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
            category = defaultCategories[0],
            walletId = walletA.id
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
            ),
            userId
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
        val transactionToAdd = transaction(
            category = defaultCategories[0],
            date = DateTime.now(),
            walletId = walletA.id
        )

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
            category = defaultCategories[0],
            walletId = walletA.id
        )

        remoteTransactionDatastore.updateWith(
            listOf(
                partialTransactionMapper.mapToEntity(transactionA).copy(
                    syncStatus = SyncStatus.ToAdd
                )
            ),
            userId
        ).blockingAwait()

        syncer.syncAll().blockingAwait()

        val localTransactions = localTransactionDatastore.getAll().blockingFirst()
        expectThat(localTransactions.size).isEqualTo(1)
    }
}
