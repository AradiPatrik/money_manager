package com.aradipatrik.integration

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.aradipatrik.data.mapper.SyncStatus
import com.aradipatrik.data.mocks.DataLayerMocks.categoryEntity
import com.aradipatrik.data.mocks.DataLayerMocks.walletDataModel
import com.aradipatrik.data.model.CategoryDataModel
import com.aradipatrik.domain.model.UserCredentials
import com.aradipatrik.integration.firebase.utils.FirestoreUtils
import com.aradipatrik.remote.auth.FirebaseAuthenticator
import com.aradipatrik.remote.data.FirestoreRemoteCategoryDatastore
import com.aradipatrik.remote.data.FirestoreRemoteWalletDatastore
import com.aradipatrik.remote.mapper.FirebaseErrorMapper
import com.aradipatrik.remote.mapper.FirebaseUserMapper
import com.aradipatrik.remote.payloadfactory.CategoryPayloadFactory
import com.aradipatrik.remote.payloadfactory.CategoryResponseConverter
import com.aradipatrik.remote.payloadfactory.WalletPayloadFactory
import com.aradipatrik.remote.payloadfactory.WalletResponseConverter
import com.google.firebase.FirebaseApp
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import strikt.api.expectThat
import strikt.assertions.all
import strikt.assertions.hasSize
import strikt.assertions.isEmpty
import strikt.assertions.isEqualTo
import strikt.assertions.isGreaterThan
import strikt.assertions.isLessThan
import strikt.assertions.isNotEmpty
import strikt.assertions.isNotEqualTo

@RunWith(AndroidJUnit4::class)
class FirestoreRemoteCategoryDatastoreTest : KoinTest {
    companion object {
        private const val TIMESTAMP_TOLERANCE = 10
    }

    private val testModule = module {
        single { CategoryPayloadFactory() }
        single { CategoryResponseConverter() }
        single { FirestoreRemoteCategoryDatastore(get(), get()) }
        single { FirebaseUserMapper() }
        single { FirebaseErrorMapper() }
        single { FirebaseAuthenticator(get(), get()) }
        single { FirestoreRemoteWalletDatastore(get(), get()) }
        single { WalletPayloadFactory() }
        single { WalletResponseConverter() }
    }

    private val categoryDatastore: FirestoreRemoteCategoryDatastore by inject()
    private val authenticator: FirebaseAuthenticator by inject()
    private val walletDatastore: FirestoreRemoteWalletDatastore by inject()

    private lateinit var userId: String

    private var walletA = walletDataModel(name = "walletA", syncStatus = SyncStatus.ToAdd)
    private var walletB = walletDataModel(name = "walletB", syncStatus = SyncStatus.ToAdd)

    @Before
    fun setup() {
        startKoin { modules(testModule) }
        FirebaseApp.initializeApp(InstrumentationRegistry.getInstrumentation().context)
        userId = authenticator.registerUserWithCredentials(
            UserCredentials("aradipatrik2@gmail.com", "Almafa123")
        ).blockingGet().id
        walletDatastore.updateWith(listOf(walletA, walletB), userId).blockingAwait()
        val list = walletDatastore.getAfter(0, userId).blockingGet()
        walletA = walletA.copy(id = list.first { it.name == walletA.name }.id)
        walletB = walletB.copy(id = list.first { it.name == walletB.name }.id)
    }

    @After
    fun teardown() {
        categoryDatastore.blockingCleanupUserCategories(userId)
        FirestoreUtils.deleteWalletsOfUser(userId)
        FirestoreUtils.deleteUserById(userId)
        FirestoreUtils.removeAuthenticatedUser()
        stopKoin()
    }

    @Test
    fun updateWithToAdd() {
        val itemsToAdd = listOf(
            categoryEntity(name = "A", syncStatus = SyncStatus.ToAdd, walletId = walletA.id),
            categoryEntity(name = "B", syncStatus = SyncStatus.ToAdd, walletId = walletB.id)
        )

        val beforeUpdateTime = System.currentTimeMillis()
        categoryDatastore.updateWith(itemsToAdd, userId).blockingAwait()
        val resultEntities = categoryDatastore.getAfter(0, userId)
            .blockingGet()
            .sortedBy(CategoryDataModel::name)
        val afterUpdateTime = System.currentTimeMillis()

        expectThat(resultEntities).hasSize(itemsToAdd.size)
        expectThat(resultEntities).all {
            get(CategoryDataModel::updatedTimeStamp)
                .isLessThan(afterUpdateTime)
                .isGreaterThan(beforeUpdateTime - TIMESTAMP_TOLERANCE)
        }
        itemsToAdd.zip(resultEntities).forEach { (original, result) ->
            assertAddResultSynced(original, result)
        }
    }

    @Test
    fun updateWithToUpdate() {
        val toAdd = listOf(
            categoryEntity(name = "A", syncStatus = SyncStatus.ToAdd, walletId = walletA.id),
            categoryEntity(name = "B", syncStatus = SyncStatus.ToAdd, walletId = walletA.id)
        )
        categoryDatastore.updateWith(toAdd, userId).blockingAwait()
        val addResultEntities = categoryDatastore.getAfter(0, userId)
            .blockingGet()
            .sortedBy(CategoryDataModel::id)
        val toUpdate = toAdd.zip(addResultEntities).map { (original, result) ->
            original.copy(id = result.id, name = "C", syncStatus = SyncStatus.ToUpdate)
        }

        val beforeUpdateTime = System.currentTimeMillis()
        categoryDatastore.updateWith(toUpdate, userId).blockingAwait()
        val afterUpdateTime = System.currentTimeMillis()

        val updateResultEntities = categoryDatastore.getAfter(0, userId)
            .blockingGet()
            .sortedBy(CategoryDataModel::id)

        expectThat(updateResultEntities).hasSize(2)
        expectThat(updateResultEntities).all {
            get(CategoryDataModel::updatedTimeStamp)
                .isLessThan(afterUpdateTime)
                .isGreaterThan(beforeUpdateTime - TIMESTAMP_TOLERANCE)
        }
        toUpdate.zip(updateResultEntities).forEach { (original, result) ->
            expectThat(original.iconId).isEqualTo(result.iconId)
            expectThat(original.id).isEqualTo(result.id)
            expectThat(original.name).isEqualTo(result.name)
            expectThat(result.syncStatus).isEqualTo(SyncStatus.Synced)
        }
    }

    @Test
    fun updateWithToDelete() {
        val toAdd = listOf(
            categoryEntity(name = "A", syncStatus = SyncStatus.ToAdd, walletId = walletA.id),
            categoryEntity(name = "B", syncStatus = SyncStatus.ToAdd, walletId = walletB.id)
        )
        categoryDatastore.updateWith(toAdd, userId).blockingAwait()
        val addResultEntities = categoryDatastore.getAfter(0, userId)
            .blockingGet()

        categoryDatastore.updateWith(
            addResultEntities.map { it.copy(syncStatus = SyncStatus.ToDelete) },
            userId
        )
            .blockingAwait()
        val result = categoryDatastore.getAfter(0, userId)
            .blockingGet()
        expectThat(result).all {
            get(CategoryDataModel::syncStatus).isEqualTo(SyncStatus.ToDelete)
        }
    }

    @Test
    fun getAfterWhenNothingToGet() {
        val toAdd = generateSequence { categoryEntity(walletId = walletA.id) }.take(2).toList()
        categoryDatastore.updateWith(toAdd, userId).blockingAwait()

        val result =
            categoryDatastore.getAfter(System.currentTimeMillis(), userId)
                .blockingGet()
        expectThat(result).isEmpty()
    }

    @Test
    fun getAfterWhenWeShouldGetAll() {
        val beforeAdd = System.currentTimeMillis()
        val toAdd = listOf(
            categoryEntity(name = "A", syncStatus = SyncStatus.ToAdd, walletId = walletA.id),
            categoryEntity(name = "B", syncStatus = SyncStatus.ToAdd, walletId = walletB.id)
        )
        categoryDatastore.updateWith(toAdd, userId).blockingAwait()

        val result = categoryDatastore.getAfter(beforeAdd, userId).blockingGet()
            .sortedBy(CategoryDataModel::name)
        expectThat(result).hasSize(2)
        toAdd.zip(result).forEach { (original, result) ->
            assertAddResultSynced(original, result)
        }
    }

    private fun assertAddResultSynced(
        original: CategoryDataModel,
        result: CategoryDataModel
    ) {
        expectThat(original.name).isEqualTo(result.name)
        expectThat(original.iconId).isEqualTo(result.iconId)
        expectThat(original.id).isNotEqualTo(result.id).isNotEmpty()
        expectThat(result.syncStatus).isEqualTo(SyncStatus.Synced)
    }
}
