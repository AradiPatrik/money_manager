package com.aradipatrik.remote.data

import com.aradipatrik.data.datastore.category.RemoteCategoryDatastore
import com.aradipatrik.data.mapper.SyncStatus
import com.aradipatrik.data.model.CategoryDataModel
import com.aradipatrik.remote.CATEGORIES_FIRESTORE_KEY
import com.aradipatrik.remote.UPDATED_TIMESTAMP_KEY
import com.aradipatrik.remote.WALLETS_FIRESTORE_KEY
import com.aradipatrik.remote.data.FirestoreRemoteTransactionDatastore.Companion.USERS_COLLECTION_KEY
import com.aradipatrik.remote.payloadfactory.CategoryPayloadFactory
import com.aradipatrik.remote.payloadfactory.CategoryResponseConverter
import com.aradipatrik.remote.utils.blockingDelete
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.WriteBatch
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import io.reactivex.Completable
import io.reactivex.Single
import org.joda.time.DateTime

class FirestoreRemoteCategoryDatastore(
    private val categoryPayloadFactory: CategoryPayloadFactory,
    private val categoryResponseConverter: CategoryResponseConverter
) : RemoteCategoryDatastore {

    private fun getUserWalletsCollection(userId: String) = Firebase.firestore
        .collection(USERS_COLLECTION_KEY)
        .document(userId)
        .collection(WALLETS_FIRESTORE_KEY)

    private fun getCategoriesCollectionInsideWallet(walletId: String) =
        Firebase.firestore.collection(WALLETS_FIRESTORE_KEY)
            .document(walletId)
            .collection(CATEGORIES_FIRESTORE_KEY)

    override fun updateWith(elements: List<CategoryDataModel>, userId: String) =
        Completable.create { emitter ->
            val toUpdate = elements.filter { it.syncStatus == SyncStatus.ToUpdate }
            val toAdd = elements.filter { it.syncStatus == SyncStatus.ToAdd }
            val toDelete = elements.filter { it.syncStatus == SyncStatus.ToDelete }
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

    private fun WriteBatch.doUpdate(items: List<CategoryDataModel>) {
        items.forEach { category ->
            update(
                getCategoriesCollectionInsideWallet(category.walletId).document(category.id),
                categoryPayloadFactory.createPayloadFrom(category)
            )
        }
    }

    private fun WriteBatch.doAdd(items: List<CategoryDataModel>) {
        items.forEach { category ->
            set(
                getCategoriesCollectionInsideWallet(category.walletId).document(),
                categoryPayloadFactory.createPayloadFrom(category)
            )
        }
    }

    private fun WriteBatch.doDelete(items: List<CategoryDataModel>) {
        items.forEach { category ->
            set(
                getCategoriesCollectionInsideWallet(category.walletId).document(category.id),
                categoryPayloadFactory.createPayloadFrom(category)
            )
        }
    }

    override fun getAfter(
        time: Long,
        userId: String,
        backTrackSeconds: Long
    ) = getWalletIdsOfUser(userId)
        .map { walletIds ->
            walletIds.map { walletId ->
                getAfterInsideWallet(time, backTrackSeconds, walletId)
            }
        }
        .flatMap { getCategoryOperations ->
            Single.zip(getCategoryOperations) { categoryResults ->
                @Suppress("UNCHECKED_CAST")
                val results = categoryResults.map { it as List<CategoryDataModel> }
                results.flatten()
            }
        }

    private fun getWalletIdsOfUser(userId: String) = Single.create<List<String>> { emitter ->
        getUserWalletsCollection(userId).get()
            .addOnSuccessListener { querySnapshot ->
                emitter.onSuccess(
                    querySnapshot.documents.map(DocumentSnapshot::getId)
                )
            }
            .addOnFailureListener { cause ->
                emitter.onError(cause)
            }
            .addOnCanceledListener {
                emitter.onError(CanceledException)
            }
    }

    private fun getAfterInsideWallet(
        time: Long,
        backtrackSeconds: Long,
        walletId: String
    ) = Single.create<List<CategoryDataModel>> { emitter ->
        getCategoriesCollectionInsideWallet(walletId).whereGreaterThan(
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
     */
    fun blockingCleanupUserCategories(userId: String) {
        val walletIds = getWalletIdsOfUser(userId).blockingGet()
        walletIds.forEach(::blockingCleanupCategoriesInsideWallet)
        walletIds.forEach {
            Firebase.firestore.collection(WALLETS_FIRESTORE_KEY)
                .document(it)
                .blockingDelete()
        }
    }

    /**
     * This is just here for testing, don't use in production
     */
    fun blockingCleanupCategoriesInsideWallet(walletId: String) {
        Firebase.firestore.collection(WALLETS_FIRESTORE_KEY)
            .document(walletId)
            .collection(CATEGORIES_FIRESTORE_KEY)
            .blockingDelete()
    }
}
