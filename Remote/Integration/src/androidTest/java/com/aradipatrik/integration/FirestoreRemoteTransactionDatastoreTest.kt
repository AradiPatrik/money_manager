package com.aradipatrik.integration

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.aradipatrik.data.datastore.transaction.RemoteTransactionDatastore
import com.aradipatrik.data.datastore.wallet.RemoteWalletDatastore
import com.aradipatrik.data.mapper.SyncStatus
import com.aradipatrik.data.mocks.DataLayerMocks.transactionWithIds
import com.aradipatrik.data.mocks.DataLayerMocks.walletDataModel
import com.aradipatrik.data.model.TransactionWithIdsDataModel
import com.aradipatrik.domain.interfaces.auth.Authenticator
import com.aradipatrik.domain.model.UserCredentials
import com.aradipatrik.integration.firebase.utils.FirestoreUtils
import com.aradipatrik.remote.data.FirestoreRemoteTransactionDatastore
import com.aradipatrik.remote.remoteModule
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.CollectionReference
import io.reactivex.Completable
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
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
class FirestoreRemoteTransactionDatastoreTest : KoinTest {
    companion object {
        private const val TIMESTAMP_TOLERANCE = 10
    }

    private val transactionDatastore: RemoteTransactionDatastore by inject()
    private val walletDatastore: RemoteWalletDatastore by inject()
    private val authenticator: Authenticator by inject()

    private lateinit var userId: String
    private var walletA = walletDataModel(name = "walletA", syncStatus = SyncStatus.ToAdd)
    private var walletB = walletDataModel(name = "walletB", syncStatus = SyncStatus.ToAdd)

    @Before
    fun setup() {
        startKoin { modules(remoteModule) }
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
        (transactionDatastore as FirestoreRemoteTransactionDatastore)
            .blockingCleanupUserTransactions(userId)
        FirestoreUtils.deleteWalletsOfUser(userId)
        FirestoreUtils.deleteUserById(userId)
        FirestoreUtils.removeAuthenticatedUser()
        stopKoin()
    }

    @Test
    fun updateWithToAdd() {
        val itemsToAdd = listOf(
            transactionWithIds(memo = "A", syncStatus = SyncStatus.ToAdd, walletId = walletA.id),
            transactionWithIds(memo = "B", syncStatus = SyncStatus.ToAdd, walletId = walletB.id)
        )

        val beforeUpdateTime = System.currentTimeMillis()
        transactionDatastore.updateWith(itemsToAdd, userId).blockingAwait()
        val resultEntities = transactionDatastore.getAfter(0, userId)
            .blockingGet()
            .sortedBy(TransactionWithIdsDataModel::memo)
        val afterUpdateTime = System.currentTimeMillis()

        expectThat(resultEntities).hasSize(itemsToAdd.size)
        expectThat(resultEntities).all {
            get(TransactionWithIdsDataModel::updatedTimeStamp)
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
            transactionWithIds(memo = "A", syncStatus = SyncStatus.ToAdd, walletId = walletA.id),
            transactionWithIds(memo = "B", syncStatus = SyncStatus.ToAdd, walletId = walletA.id)
        )
        transactionDatastore.updateWith(toAdd, userId).blockingAwait()
        val addResultEntities = transactionDatastore.getAfter(0, userId)
            .blockingGet()
            .sortedBy(TransactionWithIdsDataModel::id)
        val toUpdate = toAdd.zip(addResultEntities).map { (original, result) ->
            original.copy(id = result.id, memo = "C", syncStatus = SyncStatus.ToUpdate)
        }

        val beforeUpdateTime = System.currentTimeMillis()
        transactionDatastore.updateWith(toUpdate, userId).blockingAwait()
        val afterUpdateTime = System.currentTimeMillis()

        val updateResultEntities = transactionDatastore.getAfter(0, userId)
            .blockingGet()
            .sortedBy(TransactionWithIdsDataModel::id)

        expectThat(updateResultEntities).hasSize(2)
        expectThat(updateResultEntities).all {
            get(TransactionWithIdsDataModel::updatedTimeStamp)
                .isLessThan(afterUpdateTime)
                .isGreaterThan(beforeUpdateTime - TIMESTAMP_TOLERANCE)
        }
        toUpdate.zip(updateResultEntities).forEach { (original, result) ->
            expectThat(original.amount).isEqualTo(result.amount)
            expectThat(original.categoryId).isEqualTo(result.categoryId)
            expectThat(original.date).isEqualTo(result.date)
            expectThat(original.id).isEqualTo(result.id)
            expectThat(original.memo).isEqualTo(result.memo)
            expectThat(result.syncStatus).isEqualTo(SyncStatus.Synced)
        }
    }

    @Test
    fun updateWithToDelete() {
        val toAdd = listOf(
            transactionWithIds(memo = "A", syncStatus = SyncStatus.ToAdd, walletId = walletA.id),
            transactionWithIds(memo = "B", syncStatus = SyncStatus.ToAdd, walletId = walletB.id)
        )
        transactionDatastore.updateWith(toAdd, userId).blockingAwait()
        val addResultEntities = transactionDatastore.getAfter(0, userId)
            .blockingGet()

        transactionDatastore.updateWith(
            addResultEntities.map { it.copy(syncStatus = SyncStatus.ToDelete) },
            userId
        )
            .blockingAwait()
        val result = transactionDatastore.getAfter(0, userId)
            .blockingGet()
        expectThat(result).all {
            get(TransactionWithIdsDataModel::syncStatus).isEqualTo(SyncStatus.ToDelete)
        }
    }

    @Test
    fun getAfterWhenNothingToGet() {
        val toAdd = generateSequence { transactionWithIds(walletId = walletA.id) }.take(2).toList()
        transactionDatastore.updateWith(toAdd, userId).blockingAwait()

        val result =
            transactionDatastore.getAfter(System.currentTimeMillis(), userId).blockingGet()
        expectThat(result).isEmpty()
    }

    @Test
    fun getAfterWhenWeShouldGetAll() {
        val beforeAdd = System.currentTimeMillis()
        val toAdd = listOf(
            transactionWithIds(memo = "A", syncStatus = SyncStatus.ToAdd, walletId = walletA.id),
            transactionWithIds(memo = "B", syncStatus = SyncStatus.ToAdd, walletId = walletA.id)
        )
        transactionDatastore.updateWith(toAdd, userId).blockingAwait()

        val result = transactionDatastore.getAfter(beforeAdd, userId).blockingGet()
            .sortedBy(TransactionWithIdsDataModel::memo)
        expectThat(result).hasSize(2)
        toAdd.zip(result).forEach { (original, result) ->
            assertAddResultSynced(original, result)
        }
    }

    private fun assertAddResultSynced(
        original: TransactionWithIdsDataModel,
        result: TransactionWithIdsDataModel
    ) {
        expectThat(original.amount).isEqualTo(result.amount)
        expectThat(original.categoryId).isEqualTo(result.categoryId)
        expectThat(original.date).isEqualTo(result.date)
        expectThat(original.id).isNotEqualTo(result.id).isNotEmpty()
        expectThat(original.memo).isEqualTo(result.memo)
        expectThat(result.syncStatus).isEqualTo(SyncStatus.Synced)
        expectThat(original.walletId).isEqualTo(result.walletId)
    }

    private fun CollectionReference.delete() = Completable.create { con ->
        get().addOnSuccessListener {
            it.documents.forEach { document ->
                document.reference.delete()
            }
            con.onComplete()
        }
    }.blockingAwait()
}
