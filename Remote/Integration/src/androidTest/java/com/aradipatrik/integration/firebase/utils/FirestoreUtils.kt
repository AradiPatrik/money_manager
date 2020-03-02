package com.aradipatrik.integration.firebase.utils

import com.aradipatrik.remote.data.FirestoreRemoteCategoryDatastore.Companion.CATEGORIES_COLLECTION_KEY
import com.aradipatrik.remote.data.FirestoreRemoteTransactionDatastore.Companion.TRANSACTIONS_COLLECTION_KEY
import com.aradipatrik.remote.data.FirestoreRemoteTransactionDatastore.Companion.USERS_COLLECTION_KEY
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import io.reactivex.Single

object FirestoreUtils {
    fun getTransactionsOfUser(userId: String): List<DocumentSnapshot> =
        Single.create<List<DocumentSnapshot>> { e ->
            Firebase.firestore.collection(USERS_COLLECTION_KEY)
                .document(userId)
                .collection(TRANSACTIONS_COLLECTION_KEY)
                .get()
                .addOnSuccessListener {
                    e.onSuccess(it.documents)
                }
                .addOnFailureListener {
                    e.onError(it)
                }
        }.blockingGet()

    fun getCategoriesOfUser(userId: String): List<DocumentSnapshot> =
        Single.create<List<DocumentSnapshot>> { e ->
            Firebase.firestore.collection(USERS_COLLECTION_KEY)
                .document(userId)
                .collection(CATEGORIES_COLLECTION_KEY)
                .get()
                .addOnSuccessListener {
                    e.onSuccess(it.documents)
                }
                .addOnFailureListener {
                    e.onError(it)
                }
        }.blockingGet()
}
