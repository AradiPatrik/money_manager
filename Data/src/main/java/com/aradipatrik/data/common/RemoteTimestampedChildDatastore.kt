package com.aradipatrik.data.common

import io.reactivex.Completable
import io.reactivex.Single

interface RemoteTimestampedChildDatastore<E, ParentId> {
    fun updateWith(elements: List<E>, parentId: ParentId): Completable
    fun getAfter(time: Long, backTrackSeconds: Long, parentId: ParentId): Single<List<E>>
}
