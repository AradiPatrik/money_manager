package com.aradipatrik.syncintegration

import android.preference.PreferenceManager
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.aradipatrik.data.dataModule
import com.aradipatrik.data.datastore.category.LocalCategoryDatastore
import com.aradipatrik.data.datastore.category.RemoteCategoryDatastore
import com.aradipatrik.data.datastore.wallet.RemoteWalletDatastore
import com.aradipatrik.data.mapper.CategoryMapper
import com.aradipatrik.data.mapper.SyncStatus
import com.aradipatrik.data.mocks.DataLayerMocks.categoryEntity
import com.aradipatrik.data.mocks.DataLayerMocks.walletDataModel
import com.aradipatrik.domain.interfaces.auth.Authenticator
import com.aradipatrik.domain.interfaces.data.CategoryRepository
import com.aradipatrik.domain.interfaces.data.UserRepository
import com.aradipatrik.domain.mocks.DomainLayerMocks.category
import com.aradipatrik.domain.model.User
import com.aradipatrik.domain.model.UserCredentials
import com.aradipatrik.integration.firebase.utils.FirestoreUtils
import com.aradipatrik.local.database.TransactionDatabase
import com.aradipatrik.local.database.localModule
import com.aradipatrik.remote.data.FirestoreRemoteCategoryDatastore
import com.aradipatrik.remote.remoteModule
import com.f2prateek.rx.preferences2.RxSharedPreferences
import com.google.firebase.FirebaseApp
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
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
class CategorySyncIntegration : KoinTest {

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

    private val localCategoryDatastore: LocalCategoryDatastore by inject()
    private val remoteWalletDatastore: RemoteWalletDatastore by inject()
    private val categoryMapper: CategoryMapper by inject()
    private val categoryRepository: CategoryRepository by inject()
    private val authenticator: Authenticator by inject()
    private val userRepository: UserRepository by inject()

    private lateinit var userId: String
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
    fun afterAddThereShouldBeNoMorePendingCategoriesLeft() {
        categoryRepository.add(category(), walletId = walletA.id).blockingAwait()

        val pendingCategories = localCategoryDatastore.getPending().blockingGet()

        expectThat(pendingCategories).isEmpty()
    }

    @Test
    fun afterAddThereShouldBeOneEntityInLocalAndRemoteDatabases() {
        categoryRepository.add(category(), walletId = walletA.id).blockingAwait()

        val allLocal = localCategoryDatastore.getAll().test().awaitCount(1).values().first()
        expectThat(allLocal).hasSize(1)
        val allRemote = remoteCategoryDatastore.getAfter(0, userId).blockingGet()
        expectThat(allRemote).hasSize(1)
    }

    @Test
    fun afterAddTheRemoteAndTheLocalEntityShouldBeTheSame() {
        val originalCategory = category()
        categoryRepository.add(originalCategory, walletId = walletA.id).blockingAwait()

        val localResult =
            localCategoryDatastore.getAll().test().awaitCount(1).values().first().first()
        val remoteResult = remoteCategoryDatastore.getAfter(0, userId).blockingGet().first()

        expectThat(localResult).isEqualTo(remoteResult)
    }

    @Test
    fun ifOtherDeviceAddedACategoryItShouldBeReflectedInLocalAfterRefresh() {
        val localCategory = category(name = "original")
        val otherDevicesCategory = category(name = "remote")
        categoryRepository.add(localCategory, walletId = walletA.id).blockingAwait()
        val testObserver = categoryRepository.getAll(walletA.id).test()
        val afterLocalAdd = testObserver.awaitCount(1).values().first()
        expectThat(afterLocalAdd).hasSize(1)

        remoteCategoryDatastore.updateWith(
            listOf(
                categoryMapper.mapToEntity(otherDevicesCategory).copy(
                    syncStatus = SyncStatus.ToAdd, walletId = walletA.id
                )
            ),
            userId
        ).blockingAwait()

        val afterRemoteChangeAndSync =
            categoryRepository.getAll(walletA.id).test().awaitCount(1).values().first()
        val lastValue = testObserver.values().last()
        expectThat(afterRemoteChangeAndSync).hasSize(2)
        expectThat(lastValue).hasSize(2)
    }

    @Test
    fun ifOtherDeviceUpdatedACategoryItShouldBeReflectedInLocalAfterRefresh() {
        val localCategory = category(name = "original")
        categoryRepository.add(localCategory, walletId = walletA.id).blockingAwait()
        val testObserver = categoryRepository.getAll(walletA.id).test()
        val afterLocalAdd = testObserver.awaitCount(1).values().first()
        expectThat(afterLocalAdd).hasSize(1)
        val originalId = afterLocalAdd.first().id

        remoteCategoryDatastore.updateWith(
            listOf(
                categoryEntity(
                    id = originalId,
                    name = "updated",
                    syncStatus = SyncStatus.ToUpdate,
                    walletId = walletA.id
                )
            ),
            userId
        ).blockingAwait()

        val afterRemoteChangeAndSync =
            categoryRepository.getAll(walletA.id).test().awaitCount(1).values().first()
        val lastValue = testObserver.values().last()
        expectThat(afterRemoteChangeAndSync).hasSize(1)
        expectThat(lastValue).hasSize(1)

        expectThat(lastValue.first().name).isEqualTo("updated")
    }

    @Test
    fun ifOtherDeviceDeletedAnItemItShouldBeReflectedInLocalAfterRefresh() {
        val localCategory = category(name = "original")
        categoryRepository.add(localCategory, walletA.id).blockingAwait()
        val testObserver = categoryRepository.getAll(walletA.id).test()
        val afterLocalAdd = testObserver.awaitCount(1).values().first()
        expectThat(afterLocalAdd).hasSize(1)
        val originalId = afterLocalAdd.first().id

        remoteCategoryDatastore.updateWith(
            listOf(
                categoryEntity(
                    id = originalId,
                    syncStatus = SyncStatus.ToDelete,
                    walletId = walletA.id
                )
            ),
            userId
        ).blockingAwait()

        val afterRemoteChangeAndSync =
            categoryRepository.getAll(walletA.id).test().awaitCount(1).values().first()
        val lastValue = testObserver.values().last()
        expectThat(afterRemoteChangeAndSync).hasSize(1)
        expectThat(lastValue).hasSize(1)

        val category = localCategoryDatastore.getPending().test().values().last().first()
        expectThat(category.syncStatus).isEqualTo(SyncStatus.ToDelete)
    }
}
