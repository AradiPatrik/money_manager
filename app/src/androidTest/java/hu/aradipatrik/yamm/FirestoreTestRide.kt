package hu.aradipatrik.yamm

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


@RunWith(AndroidJUnit4::class)
class FirestoreTestRide {
    companion object {
        const val TEST_USER_DOCUMENT_KEY = "test-user"
        const val USER_COLLECTION_KEY = "users"
        const val TRANSACTION_COLLECTION_KEY = "transactions"
        const val CATEGORIES_COLLECTION_KEY = "categories"
    }

    @Before
    fun setup() {
        runBlocking {
            deleteUser()
        }
    }

    private suspend fun deleteUser() = suspendCoroutine<Task<Void>> { con ->
        val db = Firebase.firestore
        val testUsers = db.collection(USER_COLLECTION_KEY).document(TEST_USER_DOCUMENT_KEY)
        runBlocking { testUsers.collection(TRANSACTION_COLLECTION_KEY).delete() }
        runBlocking { testUsers.collection(CATEGORIES_COLLECTION_KEY).delete() }
        testUsers.delete().addOnCompleteListener {
            con.resume(it)
        }
    }

    @Test
    fun testAdd() {
        runBlocking {
            val result = add()
            assertTrue(result.isSuccessful)
        }
    }

    private suspend fun add() = suspendCoroutine<Task<DocumentReference>> { con ->
        val db = Firebase.firestore
        db.collection(USER_COLLECTION_KEY)
            .document(TEST_USER_DOCUMENT_KEY)
            .collection(TRANSACTION_COLLECTION_KEY)
            .add(
                hashMapOf(
                    "amount" to 500,
                    "memo" to "this is a memo"
                )
            ).addOnCompleteListener {
                con.resume(it)
            }
    }

    @Test
    fun deleteTest() {
        runBlocking {
            deleteUser()
        }
    }

    private suspend fun CollectionReference.delete() = suspendCoroutine<Unit> { con ->
        get().addOnSuccessListener {
            it.documents.forEach { document ->
                document.reference.delete()
            }
            con.resume(Unit)
        }
    }
}
