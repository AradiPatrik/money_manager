package com.aradipatrik.data.repository.common

import io.reactivex.Completable
import io.reactivex.Single

interface RemoteTimestampedDataStore<T> {
    fun updateWith(items: List<T>): Single<List<T>>
    fun getAfter(time: Long): Single<List<T>>
}
