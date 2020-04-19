package com.aradipatrik.domain.test

import com.aradipatrik.domain.interactor.category.GetCategoriesInteractor
import com.aradipatrik.domain.interfaces.data.CategoryRepository
import com.aradipatrik.domain.interfaces.data.WalletRepository
import com.aradipatrik.domain.model.Category
import com.aradipatrik.domain.mocks.DomainLayerMocks.category
import com.aradipatrik.domain.mocks.DomainLayerMocks.wallet
import com.aradipatrik.domain.model.Wallet
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
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

class CategoryCrudTest : KoinTest {

    private val categoryCrudModule = module {
        single<CategoryRepository> { mockk() }
        single<WalletRepository> { mockk() }
        single { GetCategoriesInteractor(get(), get()) }
    }

    private val mockCategoryRepository: CategoryRepository by inject()
    private val mockWalletRepository: WalletRepository by inject()
    private val getCategoriesInteractor: GetCategoriesInteractor by inject()

    @Before
    fun setup() {
        startKoin { modules(categoryCrudModule) }
    }

    @After
    fun teardown() {
        stopKoin()
    }

    @Test
    fun `Get categories should complete`() {
        stubSelectedWallet(wallet())
        stubGetAllCategories(listOf(category()))
        getCategoriesInteractor.get()
            .test()
            .assertComplete()
    }

    @Test
    fun `Get transaction in interval should use repository`() {
        val selectedWallet = wallet()
        val testCategories = listOf(category())
        stubGetAllCategories(testCategories)
        stubSelectedWallet(selectedWallet)
        val categoriesObservable = getCategoriesInteractor.get().test()
        verify(exactly = 1) { mockCategoryRepository.getAll(any()) }
        categoriesObservable.assertValue(testCategories)
    }

    private fun stubGetAllCategories(categories: List<Category>) {
        every { mockCategoryRepository.getAll(any()) } returns Observable.just(categories)
    }

    private fun stubSelectedWallet(selectedWallet: Wallet) {
        every { mockWalletRepository.getSelectedWallet() } returns Single.just(selectedWallet)
    }
}