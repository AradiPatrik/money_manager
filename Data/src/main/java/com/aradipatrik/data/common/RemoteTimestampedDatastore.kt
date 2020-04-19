package com.aradipatrik.data.common

import io.reactivex.Completable
import io.reactivex.Single

interface RemoteTimestampedDatastore<E> {
    fun updateWith(elements: List<E>, userId: String): Completable
    fun getAfter(time: Long, userId: String, backTrackSeconds: Long = 2L): Single<List<E>>
}
