package com.aradipatrik.remote.data

import com.aradipatrik.data.datastore.transaction.RemoteTransactionDatastore
import com.aradipatrik.data.mapper.SyncStatus
import com.aradipatrik.data.model.TransactionWithIdsDataModel
import com.aradipatrik.remote.UPDATED_TIMESTAMP_KEY
import com.aradipatrik.remote.WALLETS_FIRESTORE_KEY
import com.aradipatrik.remote.payloadfactory.TransactionPayloadFactory
import com.aradipatrik.remote.payloadfactory.TransactionResponseConverter
import com.aradipatrik.remote.utils.blockingDelete
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.WriteBatch
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import io.reactivex.Completable
import io.reactivex.Single
import org.joda.time.DateTime

object CanceledException : Exception()

class FirestoreRemoteTransactionDatastore(
    private val transactionPayloadFactory: TransactionPayloadFactory,
    private val transactionResponseConverter: TransactionResponseConverter
) : RemoteTransactionDatastore {
    companion object {
        const val USERS_COLLECTION_KEY = "users"
        const val TRANSACTIONS_COLLECTION_KEY = "transaction"
    }

    private fun getUserWalletsCollection(userId: String) = Firebase.firestore
        .collection(USERS_COLLECTION_KEY)
        .document(userId)
        .collection(WALLETS_FIRESTORE_KEY)

    private fun getTransactionCollectionInsideWallet(walletId: String) =
        Firebase.firestore.collection(WALLETS_FIRESTORE_KEY)
            .document(walletId)
            .collection(TRANSACTIONS_COLLECTION_KEY)

    override fun updateWith(elements: List<TransactionWithIdsDataModel>, userId: String) = Completable.defer {
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
    }

    private fun WriteBatch.doUpdate(items: List<TransactionWithIdsDataModel>) {
        items.forEach { transaction ->
            update(
                getTransactionCollectionInsideWallet(transaction.walletId).document(transaction.id),
                transactionPayloadFactory.createPayloadFrom(transaction)
            )
        }
    }

    private fun WriteBatch.doAdd(items: List<TransactionWithIdsDataModel>) {
        items.forEach { transaction ->
            set(
                getTransactionCollectionInsideWallet(transaction.walletId).document(),
                transactionPayloadFactory.createPayloadFrom(transaction)
            )
        }
    }

    private fun WriteBatch.doDelete(items: List<TransactionWithIdsDataModel>) {
        items.forEach { transaction ->
            set(
                getTransactionCollectionInsideWallet(transaction.walletId).document(transaction.id),
                transactionPayloadFactory.createPayloadFrom(transaction)
            )
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
        .flatMap { getTransactionsOperation ->
            Single.zip(getTransactionsOperation) { categoryResults ->
                @Suppress("UNCHECKED_CAST")
                val results = categoryResults.map { it as List<TransactionWithIdsDataModel> }
                results.flatten()
            }
        }

    private fun getAfterInsideWallet(
        time: Long,
        backTrackSeconds: Long,
        walletId: String
    ) = Single.create<List<TransactionWithIdsDataModel>> { emitter ->
        getTransactionCollectionInsideWallet(walletId).whereGreaterThan(
            UPDATED_TIMESTAMP_KEY, Timestamp(
                DateTime(time - backTrackSeconds * 1000).toDate()
            )
        ).get()
            .addOnSuccessListener { querySnapshot ->
                emitter.onSuccess(
                    querySnapshot.documents.map(
                        transactionResponseConverter::mapResponseToEntity
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
    fun blockingCleanupUserTransactions(userId: String) {
        val walletIds = getWalletIdsOfUser(userId).blockingGet()
        walletIds.forEach(::blockingCleanupTransactionsInsideWallet)
        walletIds.forEach {
            Firebase.firestore.collection(WALLETS_FIRESTORE_KEY)
                .document(it)
                .blockingDelete()
        }
    }

    /**
     * This is just here for testing, don't use in production
     */
    fun blockingCleanupTransactionsInsideWallet(walletId: String) {
        Firebase.firestore.collection(WALLETS_FIRESTORE_KEY)
            .document(walletId)
            .collection(TRANSACTIONS_COLLECTION_KEY)
            .blockingDelete()
    }
}
