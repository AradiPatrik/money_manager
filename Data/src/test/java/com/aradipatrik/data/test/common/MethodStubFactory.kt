package com.aradipatrik.data.test.common

import com.aradipatrik.data.common.CrudDataStore
import com.aradipatrik.data.common.LocalTimestampedDataStore
import com.aradipatrik.data.common.RemoteTimestampedDataStore
import com.aradipatrik.testing.DomainLayerMocks.long
import io.mockk.every
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

object MethodStubFactory {
    inline fun <reified E: Any, reified Id: Any> stubCrud(
        crudDataStore: CrudDataStore<E, Id>,
        updateResponse: Completable = Completable.complete(),
        addResponse: Completable = Completable.complete(),
        deleteResponse: Completable = Completable.complete(),
        getAllResponse: Observable<List<E>> = Observable.just(emptyList())
    ) {
        every { crudDataStore.update(any()) } returns updateResponse
        every { crudDataStore.add(any()) } returns addResponse
        every { crudDataStore.delete(any()) } returns deleteResponse
        every { crudDataStore.getAll() } returns getAllResponse
    }

    fun <T> stubRemoteTimestampedDataStore(
        remoteTimestampedDataStore: RemoteTimestampedDataStore<T>,
        updateWithResponse: Completable = Completable.complete(),
        getAfterResponse: Single<List<T>> = Single.just(emptyList())
    ) {
        every { remoteTimestampedDataStore.updateWith(any()) } returns updateWithResponse
        every { remoteTimestampedDataStore.getAfter(any()) } returns getAfterResponse
    }

    fun <T> stubLocalTimestampedDataStore(
        localTimestampedDataStore: LocalTimestampedDataStore<T>,
        lastSyncTimeResponse: Single<Long> = Single.just(long()),
        getUnsyncedResponse: Single<List<T>> = Single.just(emptyList()),
        updateWithResponse: Completable = Completable.complete(),
        setSyncedResponse: Completable = Completable.complete()
    ) {
        every { localTimestampedDataStore.getLastSyncTime() } returns lastSyncTimeResponse
        every { localTimestampedDataStore.getPending() } returns getUnsyncedResponse
        every { localTimestampedDataStore.updateWith(any()) } returns updateWithResponse
        every { localTimestampedDataStore.clearPending() } returns setSyncedResponse
    }
}
