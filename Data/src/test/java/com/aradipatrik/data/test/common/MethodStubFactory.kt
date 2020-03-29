package com.aradipatrik.data.test.common

import com.aradipatrik.data.common.CrudDatastore
import com.aradipatrik.data.common.LocalTimestampedDatastore
import com.aradipatrik.data.common.RemoteTimestampedChildDatastore
import com.aradipatrik.data.common.RemoteTimestampedDatastore
import com.aradipatrik.testing.DomainLayerMocks.long
import io.mockk.every
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

object MethodStubFactory {
    inline fun <reified E : Any, reified Id : Any> stubCrud(
        crudDatastore: CrudDatastore<E, Id>,
        updateResponse: Completable = Completable.complete(),
        addResponse: Completable = Completable.complete(),
        deleteResponse: Completable = Completable.complete(),
        getAllResponse: Observable<List<E>> = Observable.just(emptyList())
    ) {
        every { crudDatastore.update(any()) } returns updateResponse
        every { crudDatastore.add(any()) } returns addResponse
        every { crudDatastore.delete(any()) } returns deleteResponse
        every { crudDatastore.getAll() } returns getAllResponse
    }

    fun <T> stubRemoteTimestampedDataStore(
        remoteTimestampedDatastore: RemoteTimestampedDatastore<T>,
        updateWithResponse: Completable = Completable.complete(),
        getAfterResponse: Single<List<T>> = Single.just(emptyList())
    ) {
        every { remoteTimestampedDatastore.updateWith(any()) } returns updateWithResponse
        every { remoteTimestampedDatastore.getAfter(any()) } returns getAfterResponse
    }

    inline fun <T, reified Id: Any> stubRemoteTimestampedChildDatastore(
        remoteTimestampedChildDatastore: RemoteTimestampedChildDatastore<T, Id>,
        updateWithResponse: Completable = Completable.complete(),
        getAfterResponse: Single<List<T>> = Single.just(emptyList())
    ) {
        every {
            remoteTimestampedChildDatastore.updateWith(
                any(),
                any()
            )
        } returns updateWithResponse
        every {
            remoteTimestampedChildDatastore.getAfter(
                any(),
                any(),
                any()
            )
        } returns getAfterResponse
    }

    fun <T> stubLocalTimestampedDataStore(
        localTimestampedDataStore: LocalTimestampedDatastore<T>,
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
