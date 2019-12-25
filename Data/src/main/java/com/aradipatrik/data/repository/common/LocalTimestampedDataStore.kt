package com.aradipatrik.data.repository.common

import io.reactivex.Completable
import io.reactivex.Single

interface LocalTimestampedDataStore<E> {
    fun updateWith(elements: List<E>): Completable
    fun getUnsynced(): Single<List<E>>
    fun setSynced(elements: List<E>): Completable
    fun getLastSyncTime(): Single<Long>
}
