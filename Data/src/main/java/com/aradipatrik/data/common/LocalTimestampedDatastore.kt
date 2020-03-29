package com.aradipatrik.data.common

import io.reactivex.Completable
import io.reactivex.Single

interface LocalTimestampedDatastore<E> {
    fun updateWith(elements: List<E>): Completable
    fun getPending(): Single<List<E>>
    fun clearPending(): Completable
    fun getLastSyncTime(): Single<Long>
}
