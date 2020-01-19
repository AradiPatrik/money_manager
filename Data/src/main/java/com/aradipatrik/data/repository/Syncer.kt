package com.aradipatrik.data.repository

import com.aradipatrik.data.datasource.category.LocalCategoryDatastore
import com.aradipatrik.data.datasource.category.RemoteCategoryDatastore
import com.aradipatrik.data.common.LocalTimestampedDataStore
import com.aradipatrik.data.common.RemoteTimestampedDataStore
import com.aradipatrik.data.datasource.transaction.LocalTransactionDatastore
import com.aradipatrik.data.datasource.transaction.RemoteTransactionDatastore
import io.reactivex.Completable
import javax.inject.Inject

class Syncer(
    private val remoteTransactionDatastore: RemoteTransactionDatastore,
    private val localTransactionDatastore: LocalTransactionDatastore,
    private val remoteCategoryDatastore: RemoteCategoryDatastore,
    private val localCategoryDatastore: LocalCategoryDatastore
) {
    fun syncAll(): Completable = sync(localCategoryDatastore, remoteCategoryDatastore)
        .andThen(sync(localTransactionDatastore, remoteTransactionDatastore))

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
