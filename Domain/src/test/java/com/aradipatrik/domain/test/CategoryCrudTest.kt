package com.aradipatrik.domain.test

import com.aradipatrik.domain.model.Category
import com.aradipatrik.domain.interfaces.data.CategoryRepository
import com.aradipatrik.domain.interactor.category.GetCategoriesInteractor
import com.aradipatrik.domain.interfaces.auth.Authenticator
import com.aradipatrik.domain.model.User
import com.aradipatrik.testing.DomainLayerMocks.category
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Observable
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import java.lang.IllegalArgumentException

class CategoryCrudTest: KoinTest {

    private val categoryCrudModule = module {
        single<CategoryRepository> { mockk() }
        single { GetCategoriesInteractor(get()) }
    }

    private val mockCategoryRepository: CategoryRepository by inject()
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
        stubGetAllCategories(listOf(category()))
        getCategoriesInteractor.get()
            .test()
            .assertComplete()
    }

    @Test
    fun `Get transaction in interval should use repository`() {
        val testCategories = listOf(category())
        stubGetAllCategories(testCategories)
        val categoriesObservable = getCategoriesInteractor.get().test()
        verify(exactly = 1) { mockCategoryRepository.getAll() }
        categoriesObservable.assertValue(testCategories)
    }

    private fun stubGetAllCategories(categories: List<Category>) {
        every { mockCategoryRepository.getAll() } returns Observable.just(categories)
    }
}