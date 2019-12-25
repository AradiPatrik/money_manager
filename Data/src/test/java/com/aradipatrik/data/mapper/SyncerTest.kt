package com.aradipatrik.data.mapper

import com.aradipatrik.data.repository.Syncer
import com.aradipatrik.data.repository.common.LocalTimestampedDataStore
import com.aradipatrik.data.repository.common.RemoteTimestampedDataStore
import com.aradipatrik.domain.test.MockDataFactory.string
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Completable
import io.reactivex.Single
import org.junit.Test

class SyncerTest {

    companion object {
        const val DEFAULT_LAST_SYNC_TIME = 5L
    }

    private val mockLocal = mockk<LocalTimestampedDataStore<String>>()
    private val mockRemote = mockk<RemoteTimestampedDataStore<String>>()

    @Test
    fun syncShouldComplete() {
        stubLocal()
        stubRemote()
        Syncer.sync(mockLocal, mockRemote)
            .test()
            .assertComplete()
    }

    @Test
    fun nothingToSync() {
        stubLocal()
        stubRemote()
        Syncer.sync(mockLocal, mockRemote).test()
        verify {
            mockLocal.getLastSyncTime()
            mockRemote.getAfter(DEFAULT_LAST_SYNC_TIME)
            mockLocal.updateWith(emptyList())
            mockLocal.getUnsynced()
            mockRemote.updateWith(emptyList())
            mockLocal.setSynced(emptyList())
        }
    }

    @Test
    fun remoteHasMoreRecentData() {
        val remoteData = listOf(string())
        stubLocal()
        stubRemote(getAfterResult = Single.just(remoteData))
        Syncer.sync(mockLocal, mockRemote).test()
        verify {
            mockRemote.updateWith(emptyList())
            mockLocal.updateWith(remoteData)
            mockLocal.setSynced(emptyList())
        }
    }

    @Test
    fun localHasMoreRecentData() {
        val localData = listOf(string())
        stubLocal(unsyncedResult = Single.just(localData))
        stubRemote()
        Syncer.sync(mockLocal, mockRemote).test()
        verify {
            mockRemote.updateWith(localData)
            mockLocal.updateWith(emptyList())
            mockLocal.setSynced(localData)
        }
    }

    @Test
    fun bothLocalAndRemoteHasUnsyncedData() {
        val localData = listOf(string())
        val remoteData = listOf(string())
        stubLocal(unsyncedResult = Single.just(localData))
        stubRemote(getAfterResult = Single.just(remoteData))
        Syncer.sync(mockLocal, mockRemote).test()
        verify {
            mockRemote.updateWith(localData)
            mockLocal.updateWith(remoteData)
            mockLocal.setSynced(localData)
        }
    }

    @Test
    fun remoteUnreachableFromTheBeginning() {
        stubLocal()
        stubRemote(getAfterResult = Single.error(Throwable()))
        Syncer.sync(mockLocal, mockRemote).test()
        verify {
            mockRemote.getAfter(any())
        }
        verify (inverse = true) {
            mockRemote.updateWith(any())
            mockLocal.updateWith(any())
            mockLocal.setSynced(any())
        }
    }

    @Test
    fun remoteUnreachableAtUpdateCall() {
        stubLocal()
        stubRemote(updateWithResult = Completable.error(Throwable()))
        Syncer.sync(mockLocal, mockRemote).test()
        verify {
            mockRemote.getAfter(DEFAULT_LAST_SYNC_TIME)
            mockLocal.updateWith(any())
        }
        verify(inverse = true) {
            mockLocal.setSynced(any())
        }
    }

    private fun stubLocal(
        lastSyncTimeResult: Single<Long> = Single.just(DEFAULT_LAST_SYNC_TIME),
        updateWithResult: Completable = Completable.complete(),
        setSyncedResult: Completable = Completable.complete(),
        unsyncedResult: Single<List<String>> = Single.just(listOf())
    ) {
        every { mockLocal.updateWith(any()) } returns updateWithResult
        every { mockLocal.setSynced(any()) } returns setSyncedResult
        every { mockLocal.getLastSyncTime() } returns lastSyncTimeResult
        every { mockLocal.getUnsynced() } returns unsyncedResult
    }

    private fun stubRemote(
        getAfterResult: Single<List<String>> = Single.just(listOf()),
        updateWithResult: Completable = Completable.complete()
    ) {
        every { mockRemote.getAfter(any()) } returns getAfterResult
        every { mockRemote.updateWith(any()) } returns updateWithResult
    }
}