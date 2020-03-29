package com.aradipatrik.data.common

import io.reactivex.Completable
import io.reactivex.Single

interface RemoteTimestampedDatastore<T> {
    fun updateWith(items: List<T>): Completable
    fun getAfter(time: Long, backtrackSeconds: Long = 2L): Single<List<T>>
}
