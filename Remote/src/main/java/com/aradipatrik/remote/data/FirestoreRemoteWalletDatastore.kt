package com.aradipatrik.remote.data

import com.aradipatrik.data.datastore.wallet.RemoteWalletDatastore
import com.aradipatrik.data.mapper.SyncStatus
import com.aradipatrik.data.model.WalletDataModel
import com.aradipatrik.remote.UPDATED_TIMESTAMP_KEY
import com.aradipatrik.remote.USERS_FIRESTORE_KEY
import com.aradipatrik.remote.WALLETS_FIRESTORE_KEY
import com.aradipatrik.remote.payloadfactory.WalletPayloadFactory
import com.aradipatrik.remote.payloadfactory.WalletResponseConverter
import com.google.firebase.Timestamp
import com.google.firebase.firestore.WriteBatch
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import io.reactivex.Completable
import io.reactivex.Single
import org.joda.time.DateTime

class FirestoreRemoteWalletDatastore(
    private val walletPayloadFactory: WalletPayloadFactory,
    private val walletResponseConverter: WalletResponseConverter
) : RemoteWalletDatastore {

    private fun userWallets(userId: String) = userDocument(userId)
        .collection(WALLETS_FIRESTORE_KEY)

    private fun userDocument(userId: String) = Firebase.firestore.collection(USERS_FIRESTORE_KEY)
        .document(userId)

    private val walletsCollection = Firebase.firestore.collection(WALLETS_FIRESTORE_KEY)

    override fun updateWith(elements: List<WalletDataModel>, userId: String) =
        Completable.create { emitter ->
            val toUpdate = elements.filter { it.syncStatus == SyncStatus.ToUpdate }
            val toAdd = elements.filter { it.syncStatus == SyncStatus.ToAdd }
            val toDelete = elements.filter { it.syncStatus == SyncStatus.ToDelete }
            Firebase.firestore.runBatch { batch ->
                batch.doUpdate(toUpdate, userId)
                batch.doAdd(toAdd, userId)
                batch.doDelete(toDelete, userId)
            }.addOnSuccessListener {
                emitter.onComplete()
            }.addOnFailureListener { cause ->
                emitter.onError(cause)
            }.addOnCanceledListener {
                emitter.onError(CanceledException)
            }
        }

    private fun WriteBatch.doUpdate(items: List<WalletDataModel>, parentId: String) {
        items.forEach { wallet ->
            update(
                walletsCollection.document(wallet.id),
                walletPayloadFactory.createPayloadFrom(wallet)
            )
            update(
                userWallets(parentId).document(wallet.id),
                walletPayloadFactory.createPayloadFrom(wallet)
            )
        }
    }

    private fun WriteBatch.doAdd(items: List<WalletDataModel>, parentId: String) {
        items.forEach { wallet ->
            val walletId = walletsCollection.document().id
            set(
                walletsCollection.document(walletId),
                walletPayloadFactory.createPayloadFrom(wallet)
            )
            set(
                userWallets(parentId).document(walletId),
                walletPayloadFactory.createPayloadFrom(wallet)
            )
        }
    }

    private fun WriteBatch.doDelete(items: List<WalletDataModel>, parentId: String) {
        items.forEach { wallet ->
            set(
                walletsCollection.document(wallet.id),
                walletPayloadFactory.createPayloadFrom(wallet)
            )
            set(
                userWallets(parentId).document(wallet.id),
                walletPayloadFactory.createPayloadFrom(wallet)
            )
        }
    }

    override fun getAfter(
        time: Long,
        userId: String,
        backTrackSeconds: Long
    ) = Single.create<List<WalletDataModel>> { emitter ->
        userWallets(userId).whereGreaterThan(
            UPDATED_TIMESTAMP_KEY, Timestamp(
                DateTime(time - backTrackSeconds * 1000).toDate()
            )
        ).get()
            .addOnSuccessListener { querySnapshot ->
                emitter.onSuccess(
                    querySnapshot.documents.map(
                        walletResponseConverter::mapResponseToEntity
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
