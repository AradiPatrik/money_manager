package com.aradipatrik.remote.utils

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import io.reactivex.Completable

fun CollectionReference.blockingDelete() = Completable.create { con ->
    get().addOnSuccessListener {
        it.documents.forEach { document ->
            document.reference.delete()
        }
        con.onComplete()
    }
}.blockingAwait()

fun DocumentReference.blockingDelete() = Completable.create { emitter ->
    this.delete()
        .addOnSuccessListener {
            emitter.onComplete()
        }
        .addOnFailureListener {
            emitter.onError(it)
        }
}.blockingAwait()
