package com.aradipatrik.data.test

import com.aradipatrik.data.mapper.TransactionMapper
import com.aradipatrik.data.model.TransactionEntity
import com.aradipatrik.data.repository.Syncer
import com.aradipatrik.data.repository.TransactionRepositoryImpl
import com.aradipatrik.data.repository.transaction.LocalTransactionDataStore
import com.aradipatrik.data.repository.transaction.RemoteTransactionDataStore
import com.aradipatrik.data.test.common.MethodStubFactory
import com.aradipatrik.data.test.common.MockDataFactory.transactionEntity
import com.aradipatrik.testing.MockDomainDataFactory.interval
import com.aradipatrik.testing.MockDomainDataFactory.string
import com.aradipatrik.testing.MockDomainDataFactory.transaction
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifyOrder
import io.reactivex.Completable
import io.reactivex.Observable
import org.junit.Test

class TransactionRepositoryImplTest {
    companion object {
        private val DEFAULT_TRANSACTION = transaction()
        private val DEFAULT_TRANSACTION_ENTITY = transactionEntity()
    }

    private val mockMapper = mockk<TransactionMapper>()
    private val mockLocal = mockk<LocalTransactionDataStore>()
    private val mockRemote = mockk<RemoteTransactionDataStore>()
    private val mockSyncer = mockk<Syncer<TransactionEntity>>()
    private val repository = TransactionRepositoryImpl(
        mockSyncer, mockMapper, mockLocal, mockRemote
    )

    @Test
    fun `Get all should complete`() {
        setupStubs()
        repository.getAll().test().assertComplete()
    }

    @Test
    fun `Get all should sync, then get all from local`() {
        val transactions = listOf(transactionEntity())
        val getAllResponseStub = {
            MethodStubFactory.stubCrud(
                mockLocal, getAllResponse = Observable.just(transactions)
            )
        }
        setupStubs(
            localTimestampedDataStoreStub = getAllResponseStub
        )
        repository.getAll().test().assertValue { it.size == 1 }
        verifyOrder {
            mockSyncer.sync(mockLocal, mockRemote)
            mockLocal.getAll()
            mockMapper.mapFromEntity(transactions[0])
        }
    }

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
            mockSyncer.sync(mockLocal, mockRemote)
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
            mockMapper.mapToEntity(transaction)
            mockLocal.update(any())
            mockSyncer.sync(mockLocal, mockRemote)
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
            mockMapper.mapToEntity(transaction)
            mockLocal.add(DEFAULT_TRANSACTION_ENTITY)
            mockSyncer.sync(mockLocal, mockRemote)
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
        val transactions = listOf(transactionEntity())
        val interval = interval()
        every { mockLocal.getInInterval(any()) } returns Observable.just(transactions)
        repository.getInInterval(interval).test().assertValue { it.size == 1 }
        verify {
            mockSyncer.sync(mockLocal, mockRemote)
            mockLocal.getInInterval(interval)
            mockMapper.mapFromEntity(transactions[0])
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

    private fun stubMapper() {
        every { mockMapper.mapFromEntity(any()) } returns DEFAULT_TRANSACTION
        every { mockMapper.mapToEntity(any()) } returns DEFAULT_TRANSACTION_ENTITY
    }

    private fun stubSyncer(
        syncResponse: Completable = Completable.complete()
    ) {
        every { mockSyncer.sync(mockLocal, mockRemote) } returns syncResponse
    }
}
