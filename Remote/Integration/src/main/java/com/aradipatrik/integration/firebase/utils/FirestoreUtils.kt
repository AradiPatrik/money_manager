package com.aradipatrik.integration.firebase.utils

import com.aradipatrik.remote.WALLETS_FIRESTORE_KEY
import com.aradipatrik.remote.data.FirestoreRemoteTransactionDatastore.Companion.USERS_COLLECTION_KEY
import com.aradipatrik.remote.utils.blockingDelete
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import io.reactivex.Completable

object FirestoreUtils {
    fun removeAuthenticatedUser() = Completable.create { emitter ->
        FirebaseAuth.getInstance().currentUser!!.delete()
            .addOnSuccessListener {
                emitter.onComplete()
            }
            .addOnFailureListener {
                emitter.onError(it)
            }
    }.blockingAwait()

    fun deleteWalletsOfUser(userId: String) =
        Firebase.firestore.collection(USERS_COLLECTION_KEY)
            .document(userId)
            .collection(WALLETS_FIRESTORE_KEY)
            .blockingDelete()

    fun deleteUserById(userId: String) =
        Firebase.firestore.collection(USERS_COLLECTION_KEY)
            .document(userId)
            .blockingDelete()
}
