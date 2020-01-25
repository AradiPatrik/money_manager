package com.aradipatrik.domain.test

import com.aradipatrik.domain.model.Category
import com.aradipatrik.domain.repository.CategoryRepository
import com.aradipatrik.domain.usecase.GetCategories
import com.aradipatrik.testing.DomainLayerMocks.category
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Observable
import org.junit.Test

class CategoryCrudTest {

    private val categoryRepository = mockk<CategoryRepository>()

    @Test
    fun `Get categories should complete`() {
        stubGetAllCategories(listOf(category()))
        GetCategories(categoryRepository).get()
            .test()
            .assertComplete()
    }

    @Test
    fun `Get transaction in interval should use repository`() {
        val testCategories = listOf(category())
        stubGetAllCategories(testCategories)
        val getCategories = GetCategories(categoryRepository)
        val categoriesObservable = getCategories.get().test()
        verify(exactly = 1) { categoryRepository.getAll() }
        categoriesObservable.assertValue(testCategories)
    }

    private fun stubGetAllCategories(categories: List<Category>) {
        every { categoryRepository.getAll() } returns Observable.just(categories)
    }
}