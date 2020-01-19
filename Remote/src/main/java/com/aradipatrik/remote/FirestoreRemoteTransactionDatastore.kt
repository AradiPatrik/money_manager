package com.aradipatrik.remote

import com.aradipatrik.data.datasource.transaction.RemoteTransactionDatastore
import com.aradipatrik.data.mapper.SyncStatus
import com.aradipatrik.data.model.TransactionPartialEntity
import com.aradipatrik.remote.payloadfactory.TransactionPayloadFactory
import com.aradipatrik.remote.payloadfactory.TransactionResponseConverter
import com.aradipatrik.remote.utils.delete
import com.google.firebase.Timestamp
import com.google.firebase.firestore.WriteBatch
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.joda.time.DateTime

object CanceledException : Exception()

class FirestoreRemoteTransactionDatastore(
    private val userId: String,
    private val transactionPayloadFactory: TransactionPayloadFactory,
    private val transactionResponseConverter: TransactionResponseConverter
) : RemoteTransactionDatastore {
    companion object {
        internal const val USERS_COLLECTION_KEY = "users"
        internal const val TRANSACTIONS_COLLECTION_KEY = "transaction"
    }

    private val transactionCollection = Firebase.firestore.collection(USERS_COLLECTION_KEY)
        .document(userId)
        .collection(TRANSACTIONS_COLLECTION_KEY)

    override fun updateWith(items: List<TransactionPartialEntity>) = Completable.defer {
        Completable.create { emitter ->
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
    }

    private fun WriteBatch.doUpdate(items: List<TransactionPartialEntity>) {
        items.forEach { transaction ->
            update(
                transactionCollection.document(transaction.id),
                transactionPayloadFactory.createPayloadFrom(transaction)
            )
        }
    }

    private fun WriteBatch.doAdd(items: List<TransactionPartialEntity>) {
        items.forEach { transaction ->
            set(
                transactionCollection.document(),
                transactionPayloadFactory.createPayloadFrom(transaction)
            )
        }
    }

    private fun WriteBatch.doDelete(items: List<TransactionPartialEntity>) {
        items.forEach { transaction ->
            set(
                transactionCollection.document(transaction.id),
                transactionPayloadFactory.createPayloadFrom(transaction)
            )
        }
    }

    override fun getAfter(
        time: Long,
        backtrackSeconds: Long
    ): Single<List<TransactionPartialEntity>> =
        Single.defer {
            Single.create<List<TransactionPartialEntity>> { emitter ->
                transactionCollection.whereGreaterThan(
                    UPDATED_TIMESTAMP_KEY, Timestamp(
                        DateTime(time - backtrackSeconds * 1000).toDate()
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
        }

    /**
     * This is just here for testing, don't use in production
     * @throws IllegalStateException if user is not test user
     */
    fun deleteAllForTestUser() {
        check(userId == TEST_USER_ID) { "Usage of this method for real user is prohibited" }
        transactionCollection.delete()
    }
}
