package com.aradipatrik.integration

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.aradipatrik.domain.interfaces.auth.Authenticator
import com.aradipatrik.domain.model.UserCredentials
import com.aradipatrik.integration.firebase.utils.FirestoreUtils
import com.aradipatrik.remote.remoteModule
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import strikt.api.expectThat
import strikt.assertions.isNotEmpty

@RunWith(AndroidJUnit4::class)
class FirestoreAuthenticatorTest : KoinTest {
    companion object {
        val TEST_CREDENTIALS = UserCredentials("aradipatrik@gmail.com", "Almafa123")
    }

    @Before
    fun setup() {
        startKoin {
            modules(remoteModule)
        }
    }

    @After
    fun teardown() {
        stopKoin()
        FirestoreUtils.deleteCurrentUser()
    }

    private val authenticator: Authenticator by inject()

    @Test
    fun signUpWithEmailAndPasswordShouldReturnUserCredentials() {
        val user = authenticator.registerUserWithCredentials(
            TEST_CREDENTIALS
        ).blockingGet()

        expectThat(user.id).isNotEmpty()
    }
}