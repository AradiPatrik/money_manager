package com.aradipatrik.data.test

import com.aradipatrik.data.mapper.JoinedTransactionMapper
import com.aradipatrik.data.mapper.PartialTransactionMapper
import com.aradipatrik.data.repository.Syncer
import com.aradipatrik.data.repository.TransactionRepositoryImpl
import com.aradipatrik.data.datasource.transaction.LocalTransactionDatastore
import com.aradipatrik.data.datasource.transaction.RemoteTransactionDatastore
import com.aradipatrik.data.test.common.MethodStubFactory
import com.aradipatrik.testing.DataLayerMocks.joinedTransactionEntity
import com.aradipatrik.testing.DataLayerMocks.partialTransactionEntity
import com.aradipatrik.testing.DomainLayerMocks.interval
import com.aradipatrik.testing.DomainLayerMocks.string
import com.aradipatrik.testing.DomainLayerMocks.transaction
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Completable
import io.reactivex.Observable
import org.junit.Test

class TransactionRepositoryImplTest {
    companion object {
        private val DEFAULT_DOMAIN_TRANSACTION = transaction()
        private val DEFAULT_JOINED_ENTITY = joinedTransactionEntity()
        private val DEFAULT_PARTIAL_ENTITY = partialTransactionEntity()
    }

    private val mockPartialMapper = mockk<PartialTransactionMapper>()
    private val mockJoinedMapper = mockk<JoinedTransactionMapper>()
    private val mockLocal = mockk<LocalTransactionDatastore>()
    private val mockRemote = mockk<RemoteTransactionDatastore>()
    private val mockSyncer = mockk<Syncer>()
    private val repository = TransactionRepositoryImpl(
        mockSyncer, mockPartialMapper, mockJoinedMapper, mockLocal
    )

    @Test
    fun `Delete should complete`() {
        setupStubs()
        repository.delete(string()).test().assertComplete()
    }

    @Test
    fun `Delete should first delete from local, then sync`() {
        setupStubs()
        val idToDelete = string()
        repository.delete(idToDelete).test()
        verify {
            mockLocal.delete(idToDelete)
            mockSyncer.syncAll()
        }
    }

    @Test
    fun `Update should complete`() {
        setupStubs()
        repository.update(transaction()).test().assertComplete()
    }

    @Test
    fun `Update should first update local, then sync`() {
        setupStubs()
        val transaction = transaction()
        repository.update(transaction).test()
        verify {
            mockPartialMapper.mapToEntity(transaction)
            mockLocal.update(any())
            mockSyncer.syncAll()
        }
    }

    @Test
    fun `Add should complete`() {
        setupStubs()
        repository.add(transaction()).test().assertComplete()
    }

    @Test
    fun `Add should add item locally then, it should synchronise`() {
        setupStubs()
        val transaction = transaction()
        repository.add(transaction).test()
        verify {
            mockPartialMapper.mapToEntity(transaction)
            mockLocal.add(DEFAULT_PARTIAL_ENTITY)
            mockSyncer.syncAll()
        }
    }

    @Test
    fun `Get in interval should complete`() {
        setupStubs()
        every { mockLocal.getInInterval(any()) } returns Observable.just(emptyList())
        repository.getInInterval(interval()).test().assertComplete()
    }

    @Test
    fun `Get in interval should sync, then get in interval from local`() {
        setupStubs()
        val transactions = listOf(joinedTransactionEntity())
        val interval = interval()
        every { mockLocal.getInInterval(any()) } returns Observable.just(transactions)
        repository.getInInterval(interval).test().assertValue { it.size == 1 }
        verify {
            mockSyncer.syncAll()
            mockLocal.getInInterval(interval)
            mockJoinedMapper.mapFromEntity(transactions[0])
        }
    }

    private fun setupStubs(
        partialMapperStub: () -> Unit = { stubPartialMapper() },
        joinedMapperStub: () -> Unit = { stubJoinedMapper() },
        syncerStub: () -> Unit = { stubSyncer() },
        localCrudStub: () -> Unit = { MethodStubFactory.stubCrud(mockLocal) },
        localTimestampedDataStoreStub: () -> Unit = {
            MethodStubFactory.stubLocalTimestampedDataStore(mockLocal)
        },
        remoteTimestampedDataStoreStub: () -> Unit = {
            MethodStubFactory.stubRemoteTimestampedDataStore(mockRemote)
        }
    ) {
        partialMapperStub()
        joinedMapperStub()
        syncerStub()
        localCrudStub()
        localTimestampedDataStoreStub()
        remoteTimestampedDataStoreStub()
    }

    private fun stubPartialMapper() {
        every { mockPartialMapper.mapToEntity(any()) } returns DEFAULT_PARTIAL_ENTITY
    }

    private fun stubJoinedMapper() {
        every { mockJoinedMapper.mapFromEntity(any()) } returns DEFAULT_DOMAIN_TRANSACTION
        every { mockJoinedMapper.mapToEntity(any()) } returns DEFAULT_JOINED_ENTITY
    }

    private fun stubSyncer(
        syncResponse: Completable = Completable.complete()
    ) {
        every { mockSyncer.syncAll() } returns syncResponse
    }
}
