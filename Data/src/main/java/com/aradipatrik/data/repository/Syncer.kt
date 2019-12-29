package com.aradipatrik.data.repository

import com.aradipatrik.data.repository.category.LocalCategoryDataStore
import com.aradipatrik.data.repository.category.RemoteCategoryDataStore
import com.aradipatrik.data.repository.common.LocalTimestampedDataStore
import com.aradipatrik.data.repository.common.RemoteTimestampedDataStore
import com.aradipatrik.data.repository.transaction.LocalTransactionDataStore
import com.aradipatrik.data.repository.transaction.RemoteTransactionDataStore
import io.reactivex.Completable
import javax.inject.Inject

class Syncer @Inject constructor(
    private val remoteTransactionDataStore: RemoteTransactionDataStore,
    private val localTransactionDataStore: LocalTransactionDataStore,
    private val remoteCategoryDataStore: RemoteCategoryDataStore,
    private val localCategoryDataStore: LocalCategoryDataStore
) {
    fun syncAll(): Completable = sync(localCategoryDataStore, remoteCategoryDataStore)
        .andThen(sync(localTransactionDataStore, remoteTransactionDataStore))

    internal fun <E> sync(
        local: LocalTimestampedDataStore<E>,
        remote: RemoteTimestampedDataStore<E>
    ): Completable =
        local.getLastSyncTime()
            .flatMap { remote.getAfter(it) }
            .flatMap {
                local.updateWith(it)
                    .andThen(local.getPending())
            }
            .flatMapCompletable { remote.updateWith(it) }
            .andThen(local.clearPending())
            .andThen(local.getLastSyncTime())
            .flatMap { remote.getAfter(it) }
            .flatMapCompletable { local.updateWith(it) }
}
