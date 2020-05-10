package com.aradipatrik.domain.test

import com.aradipatrik.domain.exceptions.auth.PasswordTooShort
import com.aradipatrik.domain.interactor.onboard.SignUpWithEmailAndPasswordInteractor
import com.aradipatrik.domain.interactor.onboard.SignUpWithEmailAndPasswordInteractor.Params.Companion.forEmailAndPassword
import com.aradipatrik.domain.interfaces.auth.Authenticator
import com.aradipatrik.domain.interfaces.data.UserRepository
import com.aradipatrik.domain.model.User
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Completable
import io.reactivex.Single
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject

class SignUpFlowTest : KoinTest {
    companion object {
        const val validEmail = "any@any.com"
        const val validPassword = "123456"
        const val tooShortPassword = "12345"
    }

    private val signUpFlowTestModule = module {
        single<Authenticator> { mockk() }
        single<UserRepository> { mockk() }
        single {
            SignUpWithEmailAndPasswordInteractor(get(), get())
        }
    }

    private val mockAuthenticator: Authenticator by inject()
    private val mockUserRepository: UserRepository by inject()
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
    fun `signUpWithEmailAndPassword should complete if credentials are valid and authenticator and persist completes`() {
        stubAuthenticatorResult(Single.just(User("testUserId")))
        stubUserRepositoryResult(Completable.complete())
        emailPasswordInteractor.get(forEmailAndPassword(validEmail, validPassword))
            .test()
            .assertComplete()
    }

    @Test
    fun `signUpWithEmailAndPassword should return validation error if password is shorter then 6 chars`() {
        emailPasswordInteractor.get(forEmailAndPassword(validEmail, tooShortPassword))
            .test()
            .assertError { isPasswordTooShortException(it, min = 6, actual = 5) }
    }

    @Test
    fun `signUpWithEmailAndPassword should persist signed in user upon successful authentication`() {
        val testUser = User("testId")
        stubAuthenticatorResult(Single.just(testUser))
        stubUserRepositoryResult(Completable.complete())

        emailPasswordInteractor.get(forEmailAndPassword(validEmail, validPassword)).test()

        verify(exactly = 1) { mockUserRepository.setSignedInUser(testUser) }
    }

    @Test
    fun `setSignedInUser should not be called if exception occured during authentication`() {
        val error = Throwable()
        stubAuthenticatorResult(Single.error(error))

        emailPasswordInteractor.get(forEmailAndPassword(validEmail, validPassword)).test()
            .assertError(error)

        verify(inverse = true) { mockUserRepository.setSignedInUser(any()) }
    }

    private fun stubAuthenticatorResult(result: Single<User>) {
        every { mockAuthenticator.registerUserWithCredentials(any()) } returns result
    }

    private fun stubUserRepositoryResult(result: Completable) {
        every { mockUserRepository.setSignedInUser(any()) } returns result
    }

    @Suppress("SameParameterValue")
    private fun isPasswordTooShortException(ex: Throwable, min: Int, actual: Int) =
        ex is PasswordTooShort && ex.min == min && ex.actual == ex.actual
}
