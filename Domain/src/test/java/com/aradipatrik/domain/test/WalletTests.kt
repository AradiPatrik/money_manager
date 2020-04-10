package com.aradipatrik.domain.test

import com.aradipatrik.domain.exceptions.wallet.WalletNotFoundException
import com.aradipatrik.domain.interactor.wallet.SelectWalletInteractor
import com.aradipatrik.domain.interfaces.data.WalletRepository
import com.aradipatrik.domain.mocks.DomainLayerMocks.wallet
import com.aradipatrik.domain.model.Wallet
import com.aradipatrik.domain.store.SelectedWalletStore
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

typealias WalletParams = SelectWalletInteractor.Params

class WalletTests : KoinTest {
    private val module = module {
        single { SelectWalletInteractor(get(), get()) }
        single { SelectedWalletStore() }
        single<WalletRepository> { mockk() }
    }

    private val selectWallet: SelectWalletInteractor by inject()
    private val selectedWalletStore: SelectedWalletStore by inject()
    private val mockWalletRepository: WalletRepository by inject()

    @Before
    fun setup() {
        startKoin { modules(module) }
    }

    @After
    fun teardown() {
        stopKoin()
    }

    @Test(expected = IllegalArgumentException::class)
    fun `selectWallet should throw IllegalArgumentException if no walletid was passed`() {
        selectWallet.get().test()
    }

    @Test
    fun `selectWallet should complete if requested existing wallet id`() {
        val testWallet = wallet()
        stubGetWalletsResponse(Single.just(listOf(testWallet)))
        selectWallet.get(WalletParams.forWallet(testWallet.id))
            .test()
            .assertComplete()
    }

    @Test
    fun `selectWallet should result in error if there were no such wallets in the repository`() {
        stubGetWalletsResponse(Single.just(listOf(wallet())))
        selectWallet.get(WalletParams.forWallet("nonExistentId"))
            .test()
            .assertError(WalletNotFoundException::class.java)
    }

    private fun stubGetWalletsResponse(result: Single<List<Wallet>>) {
        every { mockWalletRepository.getWallets() } returns result
    }
}