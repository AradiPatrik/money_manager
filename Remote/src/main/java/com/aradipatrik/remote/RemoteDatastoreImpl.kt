package com.aradipatrik.remote

import com.aradipatrik.data.model.TransactionEntity
import com.aradipatrik.data.repository.transaction.RemoteTransactionDataStore
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

class RemoteTransactionDatastoreImpl @Inject constructor(
    val userId: String,
    val db: FirebaseFirestore
) : RemoteTransactionDataStore {
    companion object {
        internal const val USERS_COLLECTION_KEY = "users"
        internal const val TRANSACTIONS_COLLECTION_KEY = "transaction"
        internal const val CATEGORY_COLLECTION_KEY = "categories"
        internal const val TEST_USER_DOCUMENT_ID = "test-user"
    }

    override fun updateWith(items: List<TransactionEntity>): Completable {
        return Completable.create {
            items.forEach {
                db.collection(USERS_COLLECTION_KEY)
                    .document(TEST_USER_DOCUMENT_ID)
                    .collection(TRANSACTIONS_COLLECTION_KEY)
            }
        }
    }

    override fun getAfter(time: Long): Single<List<TransactionEntity>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
