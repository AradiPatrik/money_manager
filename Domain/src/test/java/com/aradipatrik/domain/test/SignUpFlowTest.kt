package com.aradipatrik.domain.test

import com.aradipatrik.domain.model.UserCredentials
import com.aradipatrik.domain.interactor.SignUpWithEmailAndPasswordInteractor
import org.junit.Test

class SignUpFlowTest {
    private lateinit var signUpWithEmailAndPasswordInteractor: SignUpWithEmailAndPasswordInteractor

    @Test
    fun `signUpWithEmailAndPassword should complete if any param is passed`() {
        signUpWithEmailAndPasswordInteractor = SignUpWithEmailAndPasswordInteractor()
        signUpWithEmailAndPasswordInteractor.get(
            SignUpWithEmailAndPasswordInteractor.Params.forCredentials(
                UserCredentials(email = "any", password = "any")
            )
        )
            .test()
            .assertComplete()
    }

    @Test(expected = IllegalArgumentException::class)
    fun `signUpWithEmailAndPassword should throw IllegalArgumentException if null is passed`() {
        signUpWithEmailAndPasswordInteractor = SignUpWithEmailAndPasswordInteractor()
        signUpWithEmailAndPasswordInteractor.get()
            .test()
    }
}