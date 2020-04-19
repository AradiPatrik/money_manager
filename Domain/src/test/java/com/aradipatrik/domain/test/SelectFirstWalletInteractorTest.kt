package com.aradipatrik.domain.test

import com.aradipatrik.domain.interactor.wallet.SelectFirstWalletInteractor
import com.aradipatrik.domain.interfaces.data.WalletRepository
import com.aradipatrik.domain.mocks.DomainLayerMocks.wallet
import com.aradipatrik.domain.model.Wallet
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
import strikt.api.expectThat

class SelectFirstWalletInteractorTest : KoinTest {
    private val testModule = module {
        single { SelectFirstWalletInteractor(get()) }
        single<WalletRepository> { mockk() }
    }

    private val repository: WalletRepository by inject()
    private val selectWalletInteractor: SelectFirstWalletInteractor by inject()

    @Before
    fun setup() {
        startKoin { modules(testModule) }
    }

    @After
    fun teardown() {
        stopKoin()
    }

    @Test
    fun `Select wallet should set the first wallet as selected from the repository`() {
        val testWallet = wallet()
        stubGetWalletsResponse(Single.just(listOf(testWallet)))
        stubSetSelectedWalletResponse(Completable.complete())

        selectWalletInteractor.get()
            .test()
            .assertComplete()

        verify {
            repository.setSelectedWallet(testWallet)
        }
    }

    private fun stubGetWalletsResponse(result: Single<List<Wallet>>) {
        every { repository.getWallets() } returns result
    }

    private fun stubSetSelectedWalletResponse(result: Completable) {
        every { repository.setSelectedWallet(any()) } returns result
    }
}
