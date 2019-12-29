package com.aradipatrik.data.test

import com.aradipatrik.data.repository.Syncer
import com.aradipatrik.data.repository.category.LocalCategoryDataStore
import com.aradipatrik.data.repository.category.RemoteCategoryDataStore
import com.aradipatrik.data.repository.common.LocalTimestampedDataStore
import com.aradipatrik.data.repository.common.RemoteTimestampedDataStore
import com.aradipatrik.data.repository.transaction.LocalTransactionDataStore
import com.aradipatrik.data.repository.transaction.RemoteTransactionDataStore
import com.aradipatrik.testing.DomainLayerMocks.string
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifyOrder
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.subjects.CompletableSubject
import org.junit.Test
import strikt.api.expectThat
import strikt.assertions.hasSize
import strikt.assertions.isFalse

class SyncerTest {

    companion object {
        const val DEFAULT_LAST_SYNC_TIME = 5L
    }

    private val mockLocal = mockk<LocalTimestampedDataStore<String>>()
    private val mockRemote = mockk<RemoteTimestampedDataStore<String>>()

    @Test
    fun `Sync should complete`() {
        stubLocal(mockLocal)
        stubRemote(mockRemote)
        mockSyncer().sync(mockLocal, mockRemote)
            .test()
            .assertComplete()
    }

    @Test
    fun `Nothing to sync`() {
        stubLocal(mockLocal)
        stubRemote(mockRemote)
        mockSyncer().sync(mockLocal, mockRemote).test()
        verify {
            mockLocal.getLastSyncTime()
            mockRemote.getAfter(DEFAULT_LAST_SYNC_TIME)
            mockLocal.updateWith(emptyList())
            mockLocal.getPending()
            mockRemote.updateWith(emptyList())
            mockLocal.clearPending()
        }
    }

    @Test
    fun `Remote has more recent data`() {
        val remoteData = listOf(string())
        stubLocal(mockLocal)
        stubRemote(mockRemote, getAfterResult = Single.just(remoteData))
        mockSyncer().sync(mockLocal, mockRemote).test()
        verify {
            mockRemote.updateWith(emptyList())
            mockLocal.updateWith(remoteData)
            mockLocal.clearPending()
        }
    }

    @Test
    fun `Local has more recent data`() {
        val localData = listOf(string())
        stubLocal(mockLocal, unsyncedResult = Single.just(localData))
        stubRemote(mockRemote)
        val testObserver = mockSyncer().sync(mockLocal, mockRemote).test()
        testObserver.assertNoErrors()
        testObserver.assertComplete()
        verify {
            mockRemote.updateWith(localData)
            mockLocal.updateWith(emptyList())
            mockLocal.clearPending()
        }
    }

    @Test
    fun `Both local and remote has unsynced data`() {
        val localData = listOf(string())
        val remoteData = listOf(string())
        stubLocal(mockLocal, unsyncedResult = Single.just(localData))
        stubRemote(mockRemote, getAfterResult = Single.just(remoteData))
        mockSyncer().sync(mockLocal, mockRemote).test()
        verify {
            mockRemote.updateWith(localData)
            mockLocal.updateWith(remoteData)
            mockLocal.clearPending()
        }
    }

    @Test
    fun `Remote unreachable from teh beginning`() {
        stubLocal(mockLocal)
        stubRemote(mockRemote, getAfterResult = Single.error(Throwable()))
        val testObserver = mockSyncer().sync(mockLocal, mockRemote).test()
        expectThat(testObserver.errors()).hasSize(1)
        testObserver.assertNotComplete()
        verify {
            mockRemote.getAfter(any())
        }
        verify(inverse = true) {
            mockRemote.updateWith(any())
            mockLocal.updateWith(any())
            mockLocal.clearPending()
        }
    }

    @Test
    fun `Remote unreachable at update call`() {
        val clearPendingResult = CompletableSubject.create()
        stubLocal(mockLocal, clearPendingResult = clearPendingResult)
        stubRemote(mockRemote, updateWithResult = Completable.error(Throwable()))
        val testObserver = mockSyncer().sync(mockLocal, mockRemote).test()
        expectThat(testObserver.errors()).hasSize(1)
        testObserver.assertNotComplete()
        expectThat(clearPendingResult.hasObservers()).isFalse()
        verify {
            mockRemote.getAfter(DEFAULT_LAST_SYNC_TIME)
            mockLocal.updateWith(any())
        }
    }

    @Test
    fun `Syncer should first sync categories, then it should sync transactions`() {
        val mockLocalTransactionDataStore = mockk<LocalTransactionDataStore>()
        val mockRemoteTransactionDataStore = mockk<RemoteTransactionDataStore>()
        val mockLocalCategoryDataStore = mockk<LocalCategoryDataStore>()
        val mockRemoteCategoryDataStore = mockk<RemoteCategoryDataStore>()
        stubLocal(mockLocalTransactionDataStore)
        stubLocal(mockLocalCategoryDataStore)
        stubRemote(mockRemoteTransactionDataStore)
        stubRemote(mockRemoteCategoryDataStore)
        val testObserver = Syncer(
            mockRemoteTransactionDataStore, mockLocalTransactionDataStore,
            mockRemoteCategoryDataStore, mockLocalCategoryDataStore
        ).syncAll().test()
        testObserver.assertComplete()
        verifyOrder {
            mockLocalCategoryDataStore.updateWith(any())
            mockLocalTransactionDataStore.updateWith(any())
        }
    }

    private fun mockSyncer(): Syncer {
        return Syncer(mockk(), mockk(), mockk(), mockk())
    }

    private fun <E> stubLocal(
        local: LocalTimestampedDataStore<E>,
        lastSyncTimeResult: Single<Long> = Single.just(DEFAULT_LAST_SYNC_TIME),
        updateWithResult: Completable = Completable.complete(),
        clearPendingResult: Completable = Completable.complete(),
        unsyncedResult: Single<List<E>> = Single.just(listOf())
    ) {
        every { local.updateWith(any()) } returns updateWithResult
        every { local.clearPending() } returns clearPendingResult
        every { local.getLastSyncTime() } returns lastSyncTimeResult
        every { local.getPending() } returns unsyncedResult
    }

    private fun <E> stubRemote(
        remote: RemoteTimestampedDataStore<E>,
        getAfterResult: Single<List<E>> = Single.just(emptyList()),
        updateWithResult: Completable = Completable.complete()
    ) {
        every { remote.getAfter(any()) } returns getAfterResult
        every { remote.updateWith(any()) } returns updateWithResult
    }
}
