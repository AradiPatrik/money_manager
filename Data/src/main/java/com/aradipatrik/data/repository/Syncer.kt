package com.aradipatrik.data.repository

import com.aradipatrik.data.common.LocalTimestampedDatastore
import com.aradipatrik.data.common.RemoteTimestampedDatastore
import com.aradipatrik.data.datastore.category.LocalCategoryDatastore
import com.aradipatrik.data.datastore.category.RemoteCategoryDatastore
import com.aradipatrik.data.datastore.transaction.LocalTransactionDatastore
import com.aradipatrik.data.datastore.transaction.RemoteTransactionDatastore
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
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

    fun <E> sync(
        local: LocalTimestampedDatastore<E>,
        remote: RemoteTimestampedDatastore<E>
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

    fun <E> functionalSync(
        getLastSyncTimeOfLocal: () -> Single<Long>,
        updateRemoteWithFreshData: (freshData: List<E>) -> Completable,
        updateLocalWithFreshData: (freshData: List<E>) -> Completable,
        getFromRemoteAfterTimestamp: (timestamp: Long) -> Single<List<E>>,
        getPendingDataFromLocal: () -> Single<List<E>>,
        clearPendingFromLocal: () -> Completable
    ) = getLastSyncTimeOfLocal()
        .flatMap { localLastSyncTime ->
            semaphore.acquire()
            getFromRemoteAfterTimestamp(localLastSyncTime)
        }
        .observeOn(Schedulers.io())
        .flatMap { freshDataFromRemote ->
            updateLocalWithFreshData(freshDataFromRemote)
                .andThen(getPendingDataFromLocal())
        }
        .flatMapCompletable { pendingItemsFromLocal ->
            updateRemoteWithFreshData(
                pendingItemsFromLocal
            )
        }
        .observeOn(Schedulers.io())
        .andThen(clearPendingFromLocal())
        .andThen(getLastSyncTimeOfLocal())
        .flatMap { localLastSyncTime -> getFromRemoteAfterTimestamp(localLastSyncTime) }
        .observeOn(Schedulers.io())
        .flatMapCompletable { freshDataFromRemote -> updateLocalWithFreshData(freshDataFromRemote) }
        .doOnComplete { semaphore.release() }
}
