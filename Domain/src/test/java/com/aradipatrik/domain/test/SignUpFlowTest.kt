package com.aradipatrik.domain.test

import com.aradipatrik.domain.exceptions.auth.PasswordTooShort
import com.aradipatrik.domain.interactor.SignUpWithEmailAndPasswordInteractor
import com.aradipatrik.domain.interactor.SignUpWithEmailAndPasswordInteractor.Params.Companion.forEmailAndPassword
import com.aradipatrik.domain.interfaces.auth.Authenticator
import io.mockk.every
import io.mockk.mockk
import io.reactivex.Completable
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class SignUpFlowTest : KoinTest {
    companion object {
        const val validEmail = "any@any.com"
        const val validPassword = "123456"
    }

    private val signUpFlowTestModule = module {
        single<Authenticator> { mockk() }
        single { SignUpWithEmailAndPasswordInteractor(get()) }
    }

    private val mockAuthenticator: Authenticator by inject()
    private val emailPasswordInteractor: SignUpWithEmailAndPasswordInteractor by inject()

    @Before
    fun setup() {
        startKoin { modules(signUpFlowTestModule) }
    }

    @After
    fun teardown() {
        stopKoin()
    }

    @Test(expected = IllegalArgumentException::class)
    fun `signUpWithEmailAndPassword should throw IllegalArgumentException if null is passed`() {
        emailPasswordInteractor.get()
            .test()
    }

    @Test
    fun `signUpWithEmailAndPassword should complete if credentials are valid and authenticator completes successfully`() {
        stubAuthenticatorResult(Completable.complete())
        emailPasswordInteractor.get(forEmailAndPassword(validEmail, validPassword))
            .test()
            .assertComplete()
    }

    @Test
    fun `signUpWithEmailAndPassword should return validation error if password is shorter then 6 chars`() {
        emailPasswordInteractor.get(forEmailAndPassword(validEmail, "12345"))
            .test()
            .assertError { isPasswordTooShortException(it, min = 6, actual = 5) }
    }

    @Test
    fun `signUpWithEmailAndPassword should return Authenticator's result`() {
        val authenticatorResult = Completable.create { it.onComplete() }
        stubAuthenticatorResult(authenticatorResult)

        val interactorResult = emailPasswordInteractor
            .get(forEmailAndPassword(validEmail, validPassword))

        expectThat(interactorResult).isEqualTo(authenticatorResult)
    }

    private fun stubAuthenticatorResult(result: Completable) {
        every { mockAuthenticator.registerUserWithCredentials(any()) } returns result
    }

    @Suppress("SameParameterValue")
    private fun isPasswordTooShortException(ex: Throwable, min: Int, actual: Int) =
        ex is PasswordTooShort && ex.min == min && ex.actual == ex.actual
}
