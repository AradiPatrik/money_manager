package com.aradipatrik.data.test

import com.aradipatrik.data.mapper.CategoryMapper
import com.aradipatrik.data.model.CategoryEntity
import com.aradipatrik.data.repository.CategoryRepositoryImpl
import com.aradipatrik.data.repository.Syncer
import com.aradipatrik.data.repository.category.LocalCategoryDataStore
import com.aradipatrik.data.repository.category.RemoteCategoryDataStore
import com.aradipatrik.data.test.common.MockDataFactory.categoryEntity
import com.aradipatrik.data.test.common.MethodStubFactory
import com.aradipatrik.domain.model.Category
import com.aradipatrik.domain.repository.CategoryRepository
import com.aradipatrik.domain.test.MockDataFactory.category
import io.mockk.*
import io.reactivex.Completable
import io.reactivex.Observable
import org.junit.Test

class CategoryRepositoryImplTest {
    companion object {
        private val DEFAULT_CATEGORY = category()
        private val DEFAULT_CATEGORY_ENTITY = categoryEntity()
    }

    private val mockSyncer = mockk<Syncer<CategoryEntity>>()
    private val mockMapper = mockk<CategoryMapper>()
    private val mockRemote = mockk<RemoteCategoryDataStore>()
    private val mockLocal = mockk<LocalCategoryDataStore>()
    private val repository: CategoryRepository = CategoryRepositoryImpl(
        mockSyncer, mockMapper, mockLocal, mockRemote
    )

    @Test
    fun `Get all completes`() {
        setupStubs()
        repository.getAll()
            .test()
            .assertComplete()
    }

    @Test
    fun `Get all should sync, then it should get all from local`() {
        val categories = listOf(categoryEntity())
        setupStubs(
            localCrudStub = {
                MethodStubFactory.stubCrud(mockLocal, getAllResponse = Observable.just(categories))
            }
        )
        repository.getAll().test()
        verifyOrder {
            mockSyncer.sync(any(), any())
            mockLocal.getAll()
            mockMapper.mapFromEntity(categories[0])
        }
    }

    @Test
    fun `Add completes`() {
        setupStubs()
    }

    private fun setupStubs(
        mapperStub: () -> Unit = { stubMapper() },
        syncerStub: () -> Unit = { stubSyncer() },
        localCrudStub: () -> Unit = { MethodStubFactory.stubCrud(mockLocal) },
        localTimestampedDataStoreStub: () -> Unit = {
            MethodStubFactory.stubLocalTimestampedDataStore(mockLocal)
        },
        remoteTimestampedDataStoreStub: () -> Unit = {
            MethodStubFactory.stubRemoteTimestampedDataStore(mockRemote)
        }
    ) {
        mapperStub()
        syncerStub()
        localCrudStub()
        localTimestampedDataStoreStub()
        remoteTimestampedDataStoreStub()
    }

    private fun stubMapper(
        mapFromEntityResponse: Category = DEFAULT_CATEGORY,
        mapToEntityResponse: CategoryEntity = DEFAULT_CATEGORY_ENTITY
    ) {
        every { mockMapper.mapFromEntity(any()) } returns mapFromEntityResponse
        every { mockMapper.mapToEntity(any()) } returns mapToEntityResponse
    }

    private fun stubSyncer(
        syncResponse: Completable = Completable.complete()
    ) {
        every { mockSyncer.sync(mockLocal, mockRemote) } returns syncResponse
    }
}
