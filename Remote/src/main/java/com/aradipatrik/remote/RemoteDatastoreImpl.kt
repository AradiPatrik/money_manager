package com.aradipatrik.remote

import com.aradipatrik.data.mapper.SyncStatus
import com.aradipatrik.data.model.TransactionPartialEntity
import com.aradipatrik.data.repository.transaction.RemoteTransactionDataStore
import com.aradipatrik.remote.payloadfactory.TransactionPayloadFactory
import com.aradipatrik.remote.payloadfactory.TransactionResponsePayloadConverter
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.WriteBatch
import io.reactivex.Completable
import io.reactivex.Single
import org.joda.time.DateTime
import javax.inject.Inject

object CanceledException : Exception()

class RemoteTransactionDatastoreImpl @Inject constructor(
    userId: String,
    private val db: FirebaseFirestore,
    private val transactionPayloadFactory: TransactionPayloadFactory,
    private val transactionResponsePayloadConverter: TransactionResponsePayloadConverter
) : RemoteTransactionDataStore {
    companion object {
        internal const val USERS_COLLECTION_KEY = "users"
        internal const val TRANSACTIONS_COLLECTION_KEY = "transaction"
    }

    private val transactionCollection = db.collection(USERS_COLLECTION_KEY)
        .document(userId)
        .collection(TRANSACTIONS_COLLECTION_KEY)

    override fun updateWith(items: List<TransactionPartialEntity>) = Completable.create { emitter ->
        val toUpdate = items.filter { it.syncStatus == SyncStatus.ToUpdate }
        val toAdd = items.filter { it.syncStatus == SyncStatus.ToAdd }
        val toDelete = items.filter { it.syncStatus == SyncStatus.ToDelete }
        db.runBatch { batch ->
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

    override fun getAfter(time: Long): Single<List<TransactionPartialEntity>> =
        Single.create { emitter ->
            transactionCollection.whereGreaterThan(
                UPDATED_TIMESTAMP_KEY, Timestamp(DateTime(time).toDate())
            ).get()
                .addOnSuccessListener { querySnapshot ->
                    emitter.onSuccess(
                        querySnapshot.documents.map(
                            transactionResponsePayloadConverter::mapResponseToEntity
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
