package com.aradipatrik.data.repository

import com.aradipatrik.data.common.LocalTimestampedDataStore
import com.aradipatrik.data.common.RemoteTimestampedDataStore
import com.aradipatrik.data.datasource.category.LocalCategoryDatastore
import com.aradipatrik.data.datasource.category.RemoteCategoryDatastore
import com.aradipatrik.data.datasource.transaction.LocalTransactionDatastore
import com.aradipatrik.data.datasource.transaction.RemoteTransactionDatastore
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.Semaphore

class Syncer(
    private val remoteTransactionDatastore: RemoteTransactionDatastore,
    private val localTransactionDatastore: LocalTransactionDatastore,
    private val remoteCategoryDatastore: RemoteCategoryDatastore,
    private val localCategoryDatastore: LocalCategoryDatastore
) {
    private val semaphore = Semaphore(1)

    fun syncAll(): Completable = sync(localCategoryDatastore, remoteCategoryDatastore)
        .andThen(sync(localTransactionDatastore, remoteTransactionDatastore))

    internal fun <E> sync(
        local: LocalTimestampedDataStore<E>,
        remote: RemoteTimestampedDataStore<E>
    ): Completable =
        local.getLastSyncTime()
            .flatMap { localLastSyncTime ->
                semaphore.acquire()
                remote.getAfter(localLastSyncTime)
            }
            .observeOn(Schedulers.io())
            .flatMap { freshDataFromRemote ->
                local.updateWith(freshDataFromRemote)
                    .andThen(local.getPending())
            }
            .flatMapCompletable { pendingItemsFromLocal -> remote.updateWith(pendingItemsFromLocal) }
            .observeOn(Schedulers.io())
            .andThen(local.clearPending())
            .andThen(local.getLastSyncTime())
            .flatMap { localLastSyncTime -> remote.getAfter(localLastSyncTime) }
            .observeOn(Schedulers.io())
            .flatMapCompletable { freshDataFromRemote -> local.updateWith(freshDataFromRemote) }
            .doOnComplete { semaphore.release() }
}
