package com.aradipatrik.data.test

import com.aradipatrik.data.datastore.user.LocalUserDatastore
import com.aradipatrik.data.repository.UserRepositoryImpl
import com.aradipatrik.domain.interfaces.data.UserRepository
import com.aradipatrik.domain.model.User
import io.mockk.every
import io.mockk.mockk
import io.reactivex.Completable
import io.reactivex.CompletableEmitter
import io.reactivex.Single
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

class UserRepositoryImplTest : KoinTest {
    private val userRepositoryImplTestModule = module {
        single<LocalUserDatastore> { mockk() }
        single<UserRepository> { UserRepositoryImpl(get()) }
    }

    private val mockUserDatastore: LocalUserDatastore by inject()
    private val userRepository: UserRepository by inject()

    @Before
    fun setup() {
        startKoin { modules(userRepositoryImplTestModule) }
    }

    @After
    fun teardown() {
        stopKoin()
    }

    @Test
    fun `user repository should simply delegate to local user data source`() {
        val localGetResult = Single.just(User("testUserId"))
        val localSetResult = Completable.create(CompletableEmitter::onComplete)
        stubGetLocalUserResult(localGetResult)
        stubSetLocalUserResult(localSetResult)

        expectThat(userRepository.getSignedInUser()).isEqualTo(localGetResult)
        expectThat(userRepository.setSignedInUser(User("id"))).isEqualTo(localSetResult)
    }

    private fun stubGetLocalUserResult(result: Single<User>) {
        every { mockUserDatastore.getUser() } returns result
    }

    private fun stubSetLocalUserResult(result: Completable) {
        every { mockUserDatastore.setUser(any()) } returns result
    }
}