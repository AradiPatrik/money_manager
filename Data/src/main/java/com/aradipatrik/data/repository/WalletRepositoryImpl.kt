package com.aradipatrik.data.repository

import com.aradipatrik.data.datastore.wallet.LocalWalletDatastore
import com.aradipatrik.data.datastore.wallet.RemoteWalletDatastore
import com.aradipatrik.data.mapper.WalletMapper
import com.aradipatrik.domain.interfaces.data.UserRepository
import com.aradipatrik.domain.interfaces.data.WalletRepository
import com.aradipatrik.domain.model.User
import com.aradipatrik.domain.model.Wallet
import io.reactivex.Completable
import io.reactivex.Single

class WalletRepositoryImpl(
    private val localWalletDatastore: LocalWalletDatastore,
    private val remoteWalletDatastore: RemoteWalletDatastore,
    private val userRepository: UserRepository,
    private val syncer: Syncer,
    private val walletMapper: WalletMapper
) : WalletRepository {
    override fun addWallet(wallet: Wallet) = userRepository
        .getSignedInUser()
        .flatMapCompletable { (userId) ->
            localWalletDatastore.add(walletMapper.mapToEntity(wallet))
                .andThen(userId.let(::syncUser))
        }

    override fun updateWallet(wallet: Wallet) = userRepository
        .getSignedInUser()
        .flatMapCompletable { (userId) ->
            localWalletDatastore.update(walletMapper.mapToEntity(wallet))
                .andThen(userId.let(::syncUser))
        }

    override fun deleteWallet(walletId: String) = userRepository
        .getSignedInUser()
        .flatMapCompletable { (userId) ->
            localWalletDatastore.delete(walletId)
                .andThen(userId.let(::syncUser))
        }

    override fun getWallets() = userRepository
        .getSignedInUser()
        .flatMap { (userId) ->
            syncUser(userId)
                .andThen(localWalletDatastore.getAll().firstOrError())
        }.map { walletEntities ->
            walletEntities.map(walletMapper::mapFromEntity)
        }

    private fun syncUser(userId: String) = syncer.functionalSync(
        getLastSyncTimeOfLocal = localWalletDatastore::getLastSyncTime,
        updateLocalWithFreshData = localWalletDatastore::updateWith,
        getPendingDataFromLocal = localWalletDatastore::getPending,
        clearPendingFromLocal = localWalletDatastore::clearPending,
        updateRemoteWithFreshData = { freshData ->
            remoteWalletDatastore.updateWith(
                freshData,
                userId
            )
        },
        getFromRemoteAfterTimestamp = { timestamp ->
            remoteWalletDatastore.getAfter(
                time = timestamp,
                parentId = userId,
                backTrackSeconds = 2L
            )
        }
    )
}