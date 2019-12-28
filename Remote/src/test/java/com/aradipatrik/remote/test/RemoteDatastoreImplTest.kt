package com.aradipatrik.remote.test

import com.aradipatrik.remote.RemoteTransactionDatastoreImpl
import com.google.firebase.firestore.FirebaseFirestore
import io.mockk.every
import io.mockk.mockk
import org.junit.Test

class RemoteDatastoreImplTest {

    @Test
    fun pleaseWork() {
        val remote = RemoteTransactionDatastoreImpl("user-id", mockk())
    }
}
