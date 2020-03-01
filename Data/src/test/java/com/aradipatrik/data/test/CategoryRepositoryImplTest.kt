package com.aradipatrik.data.test

import com.aradipatrik.data.mapper.CategoryMapper
import com.aradipatrik.data.model.CategoryEntity
import com.aradipatrik.data.repository.CategoryRepositoryImpl
import com.aradipatrik.data.repository.Syncer
import com.aradipatrik.data.datasource.category.LocalCategoryDatastore
import com.aradipatrik.data.datasource.category.RemoteCategoryDatastore
import com.aradipatrik.testing.DataLayerMocks.categoryEntity
import com.aradipatrik.data.test.common.MethodStubFactory
import com.aradipatrik.domain.model.Category
import com.aradipatrik.domain.interfaces.CategoryRepository
import com.aradipatrik.testing.DomainLayerMocks.category
import com.aradipatrik.testing.DomainLayerMocks.string
import io.mockk.*
import io.reactivex.Completable
import io.reactivex.Observable
import org.junit.Test

class CategoryRepositoryImplTest {
    companion object {
        private val DEFAULT_CATEGORY = category()
        private val DEFAULT_CATEGORY_ENTITY = categoryEntity()
    }

    private val mockSyncer = mockk<Syncer>()
    private val mockMapper = mockk<CategoryMapper>()
    private val mockRemote = mockk<RemoteCategoryDatastore>()
    private val mockLocal = mockk<LocalCategoryDatastore>()
    private val repository: CategoryRepository = CategoryRepositoryImpl(
        mockSyncer, mockMapper, mockLocal
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
            mockSyncer.syncAll()
            mockLocal.getAll()
            mockMapper.mapFromEntity(categories[0])
        }
    }

    @Test
    fun `Add completes`() {
        setupStubs()
        repository.add(category()).test().assertComplete()
    }

    @Test
    fun `Add should add to local, then it should synchronise`() {
        val category = category()
        setupStubs()
        repository.add(category).test()
        verifyOrder {
            mockMapper.mapToEntity(category)
            mockLocal.add(any())
            mockSyncer.syncAll()
        }
    }

    @Test
    fun `Delete completes`() {
        setupStubs()
        val idToDelete = string()
        repository.delete(idToDelete).test().assertComplete()
    }

    @Test
    fun `Delete should first delete from local, then sync`() {
        setupStubs()
        val idToDelete = string()
        repository.delete(idToDelete).test()
        verifyOrder {
            mockLocal.delete(idToDelete)
            mockSyncer.syncAll()
        }
    }

    @Test
    fun `Update completes`() {
        setupStubs()
        val toUpdate = category()
        repository.update(toUpdate).test().assertComplete()
    }

    @Test
    fun `Update should update local, then synchronise`() {
        setupStubs()
        val toUpdate = category()
        repository.update(toUpdate).test()
        verify {
            mockMapper.mapToEntity(toUpdate)
            mockLocal.update(any())
            mockSyncer.syncAll()
        }
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
        every { mockSyncer.syncAll() } returns syncResponse
    }
}
