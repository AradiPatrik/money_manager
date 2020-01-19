package com.aradipatrik.remote

import com.aradipatrik.data.datasource.category.RemoteCategoryDatastore
import com.aradipatrik.data.mapper.SyncStatus
import com.aradipatrik.data.model.CategoryEntity
import com.aradipatrik.remote.payloadfactory.CategoryPayloadFactory
import com.aradipatrik.remote.payloadfactory.CategoryResponseConverter
import com.aradipatrik.remote.utils.delete
import com.google.firebase.Timestamp
import com.google.firebase.firestore.WriteBatch
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import io.reactivex.Completable
import io.reactivex.Single
import org.joda.time.DateTime
import javax.inject.Inject

class FirestoreRemoteCategoryDatastore(
    private val userId: String,
    private val categoryPayloadFactory: CategoryPayloadFactory,
    private val categoryResponseConverter: CategoryResponseConverter
) : RemoteCategoryDatastore {
    companion object {
        internal const val USERS_COLLECTION_KEY = "users"
        internal const val CATEGORIES_COLLECTION_KEY = "categories"
    }


    private val categoriesCollection = Firebase.firestore.collection(USERS_COLLECTION_KEY)
        .document(userId)
        .collection(CATEGORIES_COLLECTION_KEY)

    override fun updateWith(items: List<CategoryEntity>) = Completable.create { emitter ->
        val toUpdate = items.filter { it.syncStatus == SyncStatus.ToUpdate }
        val toAdd = items.filter { it.syncStatus == SyncStatus.ToAdd }
        val toDelete = items.filter { it.syncStatus == SyncStatus.ToDelete }
        Firebase.firestore.runBatch { batch ->
            batch.doUpdate(toUpdate)
            batch.doAdd(toAdd)
            batch.doDelete(toDelete)
        }.addOnSuccessListener {
            emitter.onComplete()
        }.addOnFailureListener { cause ->
            emitter.onError(cause)
        }.addOnCanceledListener {
            emitter.onError(CanceledException)
        }
    }

    private fun WriteBatch.doUpdate(items: List<CategoryEntity>) {
        items.forEach { category ->
            update(
                categoriesCollection.document(category.id),
                categoryPayloadFactory.createPayloadFrom(category)
            )
        }
    }

    private fun WriteBatch.doAdd(items: List<CategoryEntity>) {
        items.forEach { category ->
            set(
                categoriesCollection.document(),
                categoryPayloadFactory.createPayloadFrom(category)
            )
        }
    }

    private fun WriteBatch.doDelete(items: List<CategoryEntity>) {
        items.forEach { category ->
            set(
                categoriesCollection.document(category.id),
                categoryPayloadFactory.createPayloadFrom(category)
            )
        }
    }

    override fun getAfter(
        time: Long,
        backtrackSeconds: Long
    ): Single<List<CategoryEntity>> =
        Single.create { emitter ->
            categoriesCollection.whereGreaterThan(
                UPDATED_TIMESTAMP_KEY, Timestamp(
                    DateTime(time - backtrackSeconds * 1000).toDate()
                )
            ).get()
                .addOnSuccessListener { querySnapshot ->
                    emitter.onSuccess(
                        querySnapshot.documents.map(
                            categoryResponseConverter::mapResponseToEntity
                        )
                    )
                }
                .addOnFailureListener { cause ->
                    emitter.onError(cause)
                }
                .addOnCanceledListener {
                    emitter.onError(CanceledException)
                }
        }

    /**
    * This is just here for testing, don't use in production
    * @throws IllegalStateException if user is not test user
    */
    fun deleteAllForTestUser() {
        check(userId == TEST_USER_ID) { "Usage of this method for real user is prohibited" }
        categoriesCollection.delete()
    }
}
