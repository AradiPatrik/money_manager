package com.aradipatrik.integration

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.aradipatrik.data.mapper.SyncStatus
import com.aradipatrik.data.mocks.DataLayerMocks.transactionWithIds
import com.aradipatrik.data.model.TransactionWithIds
import com.aradipatrik.integration.firebase.utils.FirestoreUtils
import com.aradipatrik.remote.data.FirestoreRemoteTransactionDatastore
import com.aradipatrik.remote.data.FirestoreRemoteTransactionDatastore.Companion.TRANSACTIONS_COLLECTION_KEY
import com.aradipatrik.remote.data.FirestoreRemoteTransactionDatastore.Companion.USERS_COLLECTION_KEY
import com.aradipatrik.remote.payloadfactory.TransactionPayloadFactory
import com.aradipatrik.remote.payloadfactory.TransactionResponseConverter
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import io.reactivex.Completable
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import strikt.api.expectThat
import strikt.assertions.*

@RunWith(AndroidJUnit4::class)
class FirestoreRemoteTransactionDatastoreTest {
    companion object {
        private const val TEST_USER_DOCUMENT_KEY = "testUser"
        private const val TIMESTAMP_TOLERANCE = 10
    }

    private val testUserDocument = Firebase.firestore
        .collection(USERS_COLLECTION_KEY)
        .document(TEST_USER_DOCUMENT_KEY)

    private val transactionCollection = testUserDocument.collection(TRANSACTIONS_COLLECTION_KEY)

    private val payloadFactory = TransactionPayloadFactory()
    private val responseConverter = TransactionResponseConverter()
    private val datastore =
        FirestoreRemoteTransactionDatastore(
            TEST_USER_DOCUMENT_KEY, payloadFactory, responseConverter
        )

    @Before
    fun setup() {
        transactionCollection.delete()
    }

    @After
    fun teardown() {
        transactionCollection.delete()
    }

    @Test
    fun updateWithToAdd() {
        val itemsToAdd = listOf(
            transactionWithIds(memo = "A", syncStatus = SyncStatus.ToAdd),
            transactionWithIds(memo = "B", syncStatus = SyncStatus.ToAdd)
        )

        val beforeUpdateTime = System.currentTimeMillis()
        datastore.updateWith(itemsToAdd).blockingAwait()
        val resultEntities = FirestoreUtils.getTransactionsOfUser(TEST_USER_DOCUMENT_KEY)
            .map(responseConverter::mapResponseToEntity)
            .sortedBy(TransactionWithIds::memo)
        val afterUpdateTime = System.currentTimeMillis()

        expectThat(resultEntities).hasSize(itemsToAdd.size)
        expectThat(resultEntities).all {
            get(TransactionWithIds::updatedTimeStamp)
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
            transactionWithIds(memo = "A", syncStatus = SyncStatus.ToAdd),
            transactionWithIds(memo = "B", syncStatus = SyncStatus.ToAdd)
        )
        datastore.updateWith(toAdd).blockingAwait()
        val addResultEntities = FirestoreUtils.getTransactionsOfUser(TEST_USER_DOCUMENT_KEY)
            .map(responseConverter::mapResponseToEntity)
            .sortedBy(TransactionWithIds::id)
        val toUpdate = toAdd.zip(addResultEntities).map { (original, result) ->
            original.copy(id = result.id, memo = "C", syncStatus = SyncStatus.ToUpdate)
        }

        val beforeUpdateTime = System.currentTimeMillis()
        datastore.updateWith(toUpdate).blockingAwait()
        val afterUpdateTime = System.currentTimeMillis()

        val updateResultEntities = FirestoreUtils.getTransactionsOfUser(TEST_USER_DOCUMENT_KEY)
            .map(responseConverter::mapResponseToEntity)
            .sortedBy(TransactionWithIds::id)

        expectThat(updateResultEntities).hasSize(2)
        expectThat(updateResultEntities).all {
            get(TransactionWithIds::updatedTimeStamp)
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
            transactionWithIds(memo = "A", syncStatus = SyncStatus.ToAdd),
            transactionWithIds(memo = "B", syncStatus = SyncStatus.ToAdd)
        )
        datastore.updateWith(toAdd).blockingAwait()
        val addResultEntities = FirestoreUtils.getTransactionsOfUser(TEST_USER_DOCUMENT_KEY)
            .map(responseConverter::mapResponseToEntity)

        datastore.updateWith(addResultEntities.map { it.copy(syncStatus = SyncStatus.ToDelete) })
            .blockingAwait()
        val result = FirestoreUtils.getTransactionsOfUser(TEST_USER_DOCUMENT_KEY)
            .map(responseConverter::mapResponseToEntity)
        expectThat(result).all {
            get(TransactionWithIds::syncStatus).isEqualTo(SyncStatus.ToDelete)
        }
    }

    @Test
    fun getAfterWhenNothingToGet() {
        val toAdd = generateSequence { transactionWithIds() }.take(2).toList()
        datastore.updateWith(toAdd).blockingAwait()

        val result = datastore.getAfter(System.currentTimeMillis()).blockingGet()
        expectThat(result).isEmpty()
    }

    @Test
    fun getAfterWhenWeShouldGetAll() {
        val beforeAdd = System.currentTimeMillis()
        val toAdd = listOf(
            transactionWithIds(memo = "A", syncStatus = SyncStatus.ToAdd),
            transactionWithIds(memo = "B", syncStatus = SyncStatus.ToAdd)
        )
        datastore.updateWith(toAdd).blockingAwait()

        val result = datastore.getAfter(beforeAdd).blockingGet()
            .sortedBy(TransactionWithIds::memo)
        expectThat(result).hasSize(2)
        toAdd.zip(result).forEach { (original, result) ->
            assertAddResultSynced(original, result)
        }
    }

    private fun assertAddResultSynced(
        original: TransactionWithIds,
        result: TransactionWithIds
    ) {
        expectThat(original.amount).isEqualTo(result.amount)
        expectThat(original.categoryId).isEqualTo(result.categoryId)
        expectThat(original.date).isEqualTo(result.date)
        expectThat(original.id).isNotEqualTo(result.id).isNotEmpty()
        expectThat(original.memo).isEqualTo(result.memo)
        expectThat(result.syncStatus).isEqualTo(SyncStatus.Synced)
    }

    private fun CollectionReference.delete() = Completable.create { con ->
        get().addOnSuccessListener {
            it.documents.forEach { document ->
                document.reference.delete()
            }
            con.onComplete()
        }
    }.blockingAwait()

    @Test
    fun deleteUserShouldWork() {
        val itemsToAdd = listOf(
            transactionWithIds(memo = "A", syncStatus = SyncStatus.ToAdd),
            transactionWithIds(memo = "B", syncStatus = SyncStatus.ToAdd)
        )

        datastore.updateWith(itemsToAdd).blockingAwait()
        val addResult = FirestoreUtils.getTransactionsOfUser(TEST_USER_DOCUMENT_KEY)
        expectThat(addResult).isNotEmpty()

        datastore.deleteAllForTestUser()
        val afterDelete = FirestoreUtils.getTransactionsOfUser(TEST_USER_DOCUMENT_KEY)
        expectThat(afterDelete.isEmpty())
    }
}
