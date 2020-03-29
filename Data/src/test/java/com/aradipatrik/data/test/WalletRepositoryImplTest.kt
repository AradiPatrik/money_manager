package com.aradipatrik.data.test

import com.aradipatrik.data.datastore.wallet.LocalWalletDatastore
import com.aradipatrik.data.datastore.wallet.RemoteWalletDatastore
import com.aradipatrik.data.mapper.WalletMapper
import com.aradipatrik.data.model.CategoryEntity
import com.aradipatrik.data.model.WalletEntity
import com.aradipatrik.data.repository.Syncer
import com.aradipatrik.data.repository.UserRepositoryImpl
import com.aradipatrik.data.repository.WalletRepositoryImpl
import com.aradipatrik.data.test.common.MethodStubFactory
import com.aradipatrik.domain.interfaces.data.UserRepository
import com.aradipatrik.domain.interfaces.data.WalletRepository
import com.aradipatrik.domain.model.Category
import com.aradipatrik.domain.model.User
import com.aradipatrik.testing.DataLayerMocks
import com.aradipatrik.testing.DomainLayerMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifyOrder
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject

class WalletRepositoryImplTest : KoinTest {
    private val walletRepositoryModule = module {
        single<WalletRepository> { WalletRepositoryImpl(get(), get(), get(), get(), get()) }
        single<Syncer> { mockk() }
        single<LocalWalletDatastore> { mockk() }
        single<RemoteWalletDatastore> { mockk() }
        single<UserRepository> { mockk() }
        single { WalletMapper() }
    }

    private val walletRepository: WalletRepository by inject()
    private val mapper: WalletMapper by inject()
    private val mockSyncer: Syncer by inject()
    private val mockLocal: LocalWalletDatastore by inject()
    private val mockRemote: RemoteWalletDatastore by inject()
    private val mockUserRepository: UserRepository by inject()

    private val testUser = User("testId")

    @Before
    fun setup() {
        startKoin { modules(walletRepositoryModule) }
    }

    @After
    fun teardown() {
        stopKoin()
    }

    @Test
    fun `getWallets should sync then return result from local`() {
        setupStubs()

        walletRepository.getWallets().test()
            .assertComplete()

        verifyOrder {
            mockUserRepository.getSignedInUser()
            mockSyncer.functionalSync(
                any(),
                any<(List<WalletEntity>) -> Completable>(),
                any(),
                any(),
                any(),
                any()
            )
            mockLocal.getAll()
        }
    }

    private fun setupStubs(
        syncerStub: () -> Unit = { stubSyncer<WalletEntity>() },
        localCrudStub: () -> Unit = { MethodStubFactory.stubCrud(mockLocal) },
        localTimestampedDataStoreStub: () -> Unit = {
            MethodStubFactory.stubLocalTimestampedDataStore(mockLocal)
        },
        remoteTimestampedDataStoreStub: () -> Unit = {
            MethodStubFactory.stubRemoteTimestampedChildDatastore(mockRemote)
        },
        userRepositoryStub: () -> Unit = {
            every { mockUserRepository.getSignedInUser() } returns Single.just(testUser)
        }
    ) {
        syncerStub()
        localCrudStub()
        localTimestampedDataStoreStub()
        remoteTimestampedDataStoreStub()
        userRepositoryStub()
    }

    private fun <E> stubSyncer(
        syncResponse: Completable = Completable.complete()
    ) {
        every { mockSyncer.syncAll() } returns syncResponse
        every {
            mockSyncer.functionalSync(
                any(),
                any<(List<E>) -> Completable>(),
                any(),
                any(),
                any(),
                any()
            )
        } returns syncResponse
    }
}