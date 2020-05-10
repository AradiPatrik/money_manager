package com.aradipatrik.domain.test

import com.aradipatrik.domain.interactor.onboard.IsUserSignedInInteractor
import com.aradipatrik.domain.interfaces.data.UserRepository
import com.aradipatrik.testing.CommonMocks.boolean
import io.mockk.every
import io.mockk.mockk
import io.reactivex.Single
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject

class IsUserSignedInInteractorTest : KoinTest {
    private val testModule = module {
        single<UserRepository> { mockk() }
        single { IsUserSignedInInteractor(get()) }
    }

    private val mockUserRepository: UserRepository by inject()
    private val isUserSignedInInteractor: IsUserSignedInInteractor by inject()

    @Before
    fun setup() {
        startKoin {
            modules(testModule)
        }
    }

    @After
    fun teardown() {
        stopKoin()
    }

    @Test
    fun `It should just delegate to the repository`() {
        val response = boolean()
        stubIsUserPresentResponse(Single.just(response))
        isUserSignedInInteractor.get().test()
            .assertValue(response)
    }

    private fun stubIsUserPresentResponse(response: Single<Boolean>) {
        every { mockUserRepository.isUserSignedIn() } returns response
    }
}