package com.aradipatrik.remote.utils

import com.google.firebase.firestore.CollectionReference
import io.reactivex.Completable

fun CollectionReference.delete() = Completable.create { con ->
    get().addOnSuccessListener {
        it.documents.forEach { document ->
            document.reference.delete()
        }
        con.onComplete()
    }
}.blockingAwait()
