package com.aradipatrik.data.repository

import com.aradipatrik.data.repository.common.LocalTimestampedDataStore
import com.aradipatrik.data.repository.common.RemoteTimestampedDataStore
import io.reactivex.Completable
import io.reactivex.Single

class Syncer<E> {
    fun sync(
        local: LocalTimestampedDataStore<E>,
        remote: RemoteTimestampedDataStore<E>
    ): Completable =
        local.getLastSyncTime()
            .flatMap { remote.getAfter(it) }
            .flatMap {
                local.updateWith(it)
                    .andThen(local.getUnsynced())
            }
            .flatMap {
                remote.updateWith(it)
                    .andThen(Single.just(it))
            }
            .flatMapCompletable {
                local.setSynced(it)
            }
}
