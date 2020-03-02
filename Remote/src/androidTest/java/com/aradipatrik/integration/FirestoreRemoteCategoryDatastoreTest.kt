package com.aradipatrik.integration

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.aradipatrik.data.mapper.SyncStatus
import com.aradipatrik.data.model.CategoryEntity
import com.aradipatrik.integration.firebase.utils.FirestoreUtils
import com.aradipatrik.remote.data.FirestoreRemoteCategoryDatastore
import com.aradipatrik.remote.data.FirestoreRemoteCategoryDatastore.Companion.CATEGORIES_COLLECTION_KEY
import com.aradipatrik.remote.data.FirestoreRemoteCategoryDatastore.Companion.USERS_COLLECTION_KEY
import com.aradipatrik.remote.payloadfactory.CategoryPayloadFactory
import com.aradipatrik.remote.payloadfactory.CategoryResponseConverter
import com.aradipatrik.testing.DataLayerMocks.categoryEntity
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
class FirestoreRemoteCategoryDatastoreTest {
    companion object {
        private const val TEST_USER_DOCUMENT_KEY = "testUser"
        private const val TIMESTAMP_TOLERANCE = 10
    }

    private val testUserDocument = Firebase.firestore
        .collection(USERS_COLLECTION_KEY)
        .document(TEST_USER_DOCUMENT_KEY)

    private val categoriesCollection = testUserDocument.collection(CATEGORIES_COLLECTION_KEY)


    private val payloadFactory = CategoryPayloadFactory()
    private val responseConverter = CategoryResponseConverter()
    private val datastore =
        FirestoreRemoteCategoryDatastore(
            TEST_USER_DOCUMENT_KEY, payloadFactory, responseConverter
        )

    @Before
    fun setup() {
        categoriesCollection.delete()
    }

    @After
    fun teardown() {
        categoriesCollection.delete()
    }

    @Test
    fun updateWithToAdd() {
        val itemsToAdd = listOf(
            categoryEntity(name = "A", syncStatus = SyncStatus.ToAdd),
            categoryEntity(name = "B", syncStatus = SyncStatus.ToAdd)
        )

        val beforeUpdateTime = System.currentTimeMillis()
        datastore.updateWith(itemsToAdd).blockingAwait()
        val resultEntities = FirestoreUtils.getCategoriesOfUser(TEST_USER_DOCUMENT_KEY)
            .map(responseConverter::mapResponseToEntity)
            .sortedBy(CategoryEntity::name)
        val afterUpdateTime = System.currentTimeMillis()

        expectThat(resultEntities).hasSize(itemsToAdd.size)
        expectThat(resultEntities).all {
            get(CategoryEntity::updatedTimeStamp)
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
            categoryEntity(name = "A", syncStatus = SyncStatus.ToAdd),
            categoryEntity(name = "B", syncStatus = SyncStatus.ToAdd)
        )
        datastore.updateWith(toAdd).blockingAwait()
        val addResultEntities = FirestoreUtils.getCategoriesOfUser(TEST_USER_DOCUMENT_KEY)
            .map(responseConverter::mapResponseToEntity)
            .sortedBy(CategoryEntity::id)
        val toUpdate = toAdd.zip(addResultEntities).map { (original, result) ->
            original.copy(id = result.id, name = "C", syncStatus = SyncStatus.ToUpdate)
        }

        val beforeUpdateTime = System.currentTimeMillis()
        datastore.updateWith(toUpdate).blockingAwait()
        val afterUpdateTime = System.currentTimeMillis()

        val updateResultEntities = FirestoreUtils.getCategoriesOfUser(TEST_USER_DOCUMENT_KEY)
            .map(responseConverter::mapResponseToEntity)
            .sortedBy(CategoryEntity::id)

        expectThat(updateResultEntities).hasSize(2)
        expectThat(updateResultEntities).all {
            get(CategoryEntity::updatedTimeStamp)
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
            categoryEntity(name = "A", syncStatus = SyncStatus.ToAdd),
            categoryEntity(name = "B", syncStatus = SyncStatus.ToAdd)
        )
        datastore.updateWith(toAdd).blockingAwait()
        val addResultEntities = FirestoreUtils.getCategoriesOfUser(TEST_USER_DOCUMENT_KEY)
            .map(responseConverter::mapResponseToEntity)

        datastore.updateWith(addResultEntities.map { it.copy(syncStatus = SyncStatus.ToDelete) })
            .blockingAwait()
        val result = FirestoreUtils.getCategoriesOfUser(TEST_USER_DOCUMENT_KEY)
            .map(responseConverter::mapResponseToEntity)
        expectThat(result).all {
            get(CategoryEntity::syncStatus).isEqualTo(SyncStatus.ToDelete)
        }
    }

    @Test
    fun getAfterWhenNothingToGet() {
        val toAdd = generateSequence { categoryEntity() }.take(2).toList()
        datastore.updateWith(toAdd).blockingAwait()

        val result = datastore.getAfter(System.currentTimeMillis()).blockingGet()
        expectThat(result).isEmpty()
    }

    @Test
    fun getAfterWhenWeShouldGetAll() {
        val beforeAdd = System.currentTimeMillis()
        val toAdd = listOf(
            categoryEntity(name = "A", syncStatus = SyncStatus.ToAdd),
            categoryEntity(name = "B", syncStatus = SyncStatus.ToAdd)
        )
        datastore.updateWith(toAdd).blockingAwait()

        val result = datastore.getAfter(beforeAdd).blockingGet()
            .sortedBy(CategoryEntity::name)
        expectThat(result).hasSize(2)
        toAdd.zip(result).forEach { (original, result) ->
            assertAddResultSynced(original, result)
        }
    }

    private fun assertAddResultSynced(
        original: CategoryEntity,
        result: CategoryEntity
    ) {
        expectThat(original.name).isEqualTo(result.name)
        expectThat(original.iconId).isEqualTo(result.iconId)
        expectThat(original.id).isNotEqualTo(result.id).isNotEmpty()
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
            categoryEntity(name = "A", syncStatus = SyncStatus.ToAdd),
            categoryEntity(name = "B", syncStatus = SyncStatus.ToAdd)
        )

        datastore.updateWith(itemsToAdd).blockingAwait()
        val addResult = FirestoreUtils.getCategoriesOfUser(TEST_USER_DOCUMENT_KEY)
        expectThat(addResult).isNotEmpty()

        datastore.deleteAllForTestUser()
        val afterDelete = FirestoreUtils.getCategoriesOfUser(TEST_USER_DOCUMENT_KEY)
        expectThat(afterDelete.isEmpty())
    }
}
