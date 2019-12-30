package com.aradipatrik.integration

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.aradipatrik.data.mapper.SyncStatus
import com.aradipatrik.data.model.TransactionPartialEntity
import com.aradipatrik.integration.firebase.utils.FirestoreUtils
import com.aradipatrik.remote.RemoteTransactionDatastoreImpl
import com.aradipatrik.remote.RemoteTransactionDatastoreImpl.Companion.TRANSACTIONS_COLLECTION_KEY
import com.aradipatrik.remote.RemoteTransactionDatastoreImpl.Companion.USERS_COLLECTION_KEY
import com.aradipatrik.remote.payloadfactory.TransactionPayloadFactory
import com.aradipatrik.remote.payloadfactory.TransactionResponsePayloadConverter
import com.aradipatrik.testing.DataLayerMocks.partialTransactionEntity
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import io.reactivex.Completable
import org.joda.time.DateTime
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import strikt.api.expectThat
import strikt.assertions.*

@RunWith(AndroidJUnit4::class)
class RemoteTransactionDatastoreImplTest {
    companion object {
        private const val TEST_USER_DOCUMENT_KEY = "testUser"
        private const val TIMESTAMP_TOLERANCE = 10
    }

    private val testUserDocument = Firebase.firestore
        .collection(USERS_COLLECTION_KEY)
        .document(TEST_USER_DOCUMENT_KEY)

    private val transactionCollection = testUserDocument.collection(TRANSACTIONS_COLLECTION_KEY)

    private val payloadFactory = TransactionPayloadFactory()
    private val responseConverter = TransactionResponsePayloadConverter()
    private val datastore = RemoteTransactionDatastoreImpl(
        TEST_USER_DOCUMENT_KEY, Firebase.firestore, payloadFactory, responseConverter
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
            partialTransactionEntity(memo = "A", syncStatus = SyncStatus.ToAdd),
            partialTransactionEntity(memo = "B", syncStatus = SyncStatus.ToAdd)
        )

        val beforeUpdateTime = System.currentTimeMillis()
        datastore.updateWith(itemsToAdd).blockingAwait()
        val resultEntities = FirestoreUtils.getTransactionsOfUser(TEST_USER_DOCUMENT_KEY)
            .map(responseConverter::mapResponseToEntity)
            .sortedBy(TransactionPartialEntity::memo)
        val afterUpdateTime = System.currentTimeMillis()

        expectThat(resultEntities).hasSize(itemsToAdd.size)
        expectThat(resultEntities).all {
            get(TransactionPartialEntity::updatedTimeStamp)
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
            partialTransactionEntity(memo = "A", syncStatus = SyncStatus.ToAdd),
            partialTransactionEntity(memo = "B", syncStatus = SyncStatus.ToAdd)
        )
        datastore.updateWith(toAdd).blockingAwait()
        val addResultEntities = FirestoreUtils.getTransactionsOfUser(TEST_USER_DOCUMENT_KEY)
            .map(responseConverter::mapResponseToEntity)
            .sortedBy(TransactionPartialEntity::id)
        val toUpdate = toAdd.zip(addResultEntities).map { (original, result) ->
            original.copy(id = result.id, memo = "C", syncStatus = SyncStatus.ToUpdate)
        }

        val beforeUpdateTime = System.currentTimeMillis()
        datastore.updateWith(toUpdate).blockingAwait()
        val afterUpdateTime = System.currentTimeMillis()

        val updateResultEntities = FirestoreUtils.getTransactionsOfUser(TEST_USER_DOCUMENT_KEY)
            .map(responseConverter::mapResponseToEntity)
            .sortedBy(TransactionPartialEntity::id)

        expectThat(updateResultEntities).hasSize(2)
        expectThat(updateResultEntities).all {
            get(TransactionPartialEntity::updatedTimeStamp)
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
            partialTransactionEntity(memo = "A", syncStatus = SyncStatus.ToAdd),
            partialTransactionEntity(memo = "B", syncStatus = SyncStatus.ToAdd)
        )
        datastore.updateWith(toAdd).blockingAwait()
        val addResultEntities = FirestoreUtils.getTransactionsOfUser(TEST_USER_DOCUMENT_KEY)
            .map(responseConverter::mapResponseToEntity)

        datastore.updateWith(addResultEntities.map { it.copy(syncStatus = SyncStatus.ToDelete) })
            .blockingAwait()
        val result = FirestoreUtils.getTransactionsOfUser(TEST_USER_DOCUMENT_KEY)
            .map(responseConverter::mapResponseToEntity)
        expectThat(result).all {
            get(TransactionPartialEntity::syncStatus).isEqualTo(SyncStatus.ToDelete)
        }
    }

    @Test
    fun getAfterWhenNothingToGet() {
        val toAdd = generateSequence { partialTransactionEntity() }.take(2).toList()
        datastore.updateWith(toAdd).blockingAwait()

        val result = datastore.getAfter(System.currentTimeMillis()).blockingGet()
        expectThat(result).isEmpty()
    }

    @Test
    fun getAfterWhenWeShouldGetAll() {
        val beforeAdd = System.currentTimeMillis()
        val toAdd = listOf(
            partialTransactionEntity(memo = "A", syncStatus = SyncStatus.ToAdd),
            partialTransactionEntity(memo = "B", syncStatus = SyncStatus.ToAdd)
        )
        datastore.updateWith(toAdd).blockingAwait()

        val result = datastore.getAfter(beforeAdd).blockingGet()
            .sortedBy(TransactionPartialEntity::memo)
        expectThat(result).hasSize(2)
        toAdd.zip(result).forEach { (original, result) ->
            assertAddResultSynced(original, result)
        }
    }

    private fun assertAddResultSynced(
        original: TransactionPartialEntity,
        result: TransactionPartialEntity
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
}
