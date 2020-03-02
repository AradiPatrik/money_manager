package com.aradipatrik.domain.test

import com.aradipatrik.domain.model.Category
import com.aradipatrik.domain.interfaces.data.CategoryRepository
import com.aradipatrik.domain.interactor.GetCategoriesInteractor
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
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import java.lang.IllegalArgumentException

class CategoryCrudTest: KoinTest {

    private val categoryCrudModule = module {
        single<Authenticator> { mockk() }
        single<CategoryRepository> { mockk() }
        single<GetCategoriesInteractor> { GetCategoriesInteractor(get(), get()) }
    }

    private val mockCategoryRepository: CategoryRepository by inject()
    private val mockAuthenticator: Authenticator by inject()
    private val getCategoriesInteractor: GetCategoriesInteractor by inject()

    @Before
    fun setup() {
        startKoin { modules(categoryCrudModule) }
    }

    @After
    fun teardown() {

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

    @Test(expected = IllegalArgumentException::class)
    fun `Get categories should throw IllegalStateException if the user is not signed in`() {
        stubCurrentUser(null)
        getCategoriesInteractor.get().test()
    }

    private fun stubCurrentUser(user: User?) {
        every { mockAuthenticator.currentUser } returns user
    }

    private fun stubGetAllCategories(categories: List<Category>) {
        every { mockCategoryRepository.getAll() } returns Observable.just(categories)
    }
}