package com.aradipatrik.data.repository.common

import io.reactivex.Completable
import io.reactivex.Single

interface LocalTimestampedDataStore<E> {
    fun updateWith(elements: List<E>): Completable
    fun getPending(): Single<List<E>>
    fun clearPending(): Completable
    fun getLastSyncTime(): Single<Long>
}
