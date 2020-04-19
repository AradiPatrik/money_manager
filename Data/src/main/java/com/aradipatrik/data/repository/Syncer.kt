package com.aradipatrik.data.repository

import com.aradipatrik.data.common.LocalTimestampedDatastore
import com.aradipatrik.data.common.RemoteTimestampedDatastore
import com.aradipatrik.data.datastore.category.LocalCategoryDatastore
import com.aradipatrik.data.datastore.category.RemoteCategoryDatastore
import com.aradipatrik.data.datastore.transaction.LocalTransactionDatastore
import com.aradipatrik.data.datastore.transaction.RemoteTransactionDatastore
import com.aradipatrik.data.datastore.wallet.LocalWalletDatastore
import com.aradipatrik.data.datastore.wallet.RemoteWalletDatastore
import com.aradipatrik.domain.interfaces.data.UserRepository
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.Semaphore

class Syncer(
    private val userRepository: UserRepository,
    private val localCategoryDatastore: LocalCategoryDatastore,
    private val remoteCategoryDatastore: RemoteCategoryDatastore,
    private val localTransactionDatastore: LocalTransactionDatastore,
    private val remoteTransactionDatastore: RemoteTransactionDatastore,
    private val localWalletDatastore: LocalWalletDatastore,
    private val remoteWalletDatastore: RemoteWalletDatastore
) {
    private val semaphore = Semaphore(1)

    fun syncAll() = userRepository.getSignedInUser()
        .flatMapCompletable { user ->
            sync(localWalletDatastore, remoteWalletDatastore, user.id)
                .andThen(sync(localCategoryDatastore, remoteCategoryDatastore, user.id))
                .andThen(sync(localTransactionDatastore, remoteTransactionDatastore, user.id))
        }

    private fun <E> sync(
        local: LocalTimestampedDatastore<E>,
        remote: RemoteTimestampedDatastore<E>,
        userId: String
    ): Completable =
        local.getLastSyncTime()
            .flatMap { localLastSyncTime ->
                semaphore.acquire()
                remote.getAfter(localLastSyncTime, userId)
            }
            .observeOn(Schedulers.io())
            .flatMap { freshDataFromRemote ->
                local.updateWith(freshDataFromRemote)
                    .andThen(local.getPending())
            }
            .flatMapCompletable { pendingItemsFromLocal -> remote.updateWith(pendingItemsFromLocal, userId) }
            .observeOn(Schedulers.io())
            .andThen(local.clearPending())
            .andThen(local.getLastSyncTime())
            .flatMap { localLastSyncTime -> remote.getAfter(localLastSyncTime, userId) }
            .observeOn(Schedulers.io())
            .flatMapCompletable { freshDataFromRemote -> local.updateWith(freshDataFromRemote) }
            .doOnComplete { semaphore.release() }
}
