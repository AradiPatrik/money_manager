package com.aradipatrik.data.repository

import com.aradipatrik.data.datastore.wallet.LocalWalletDatastore
import com.aradipatrik.data.datastore.wallet.RemoteWalletDatastore
import com.aradipatrik.data.mapper.WalletMapper
import com.aradipatrik.domain.interfaces.data.WalletRepository
import com.aradipatrik.domain.model.Wallet
import io.reactivex.Completable
import io.reactivex.Single

class WalletRepositoryImpl(
    private val localWalletDatastore: LocalWalletDatastore,
    private val syncer: Syncer,
    private val walletMapper: WalletMapper
) : WalletRepository {
    override fun createWalletWithName(name: String) = addWallet(Wallet("", name))
        .andThen(getWallets())
        .map { wallets -> wallets.first { it.name == name } }

    override fun addWallet(wallet: Wallet) =
        localWalletDatastore.add(walletMapper.mapToEntity(wallet))
            .andThen(syncer.syncAll())

    override fun updateWallet(wallet: Wallet) =
        localWalletDatastore.update(walletMapper.mapToEntity(wallet))
            .andThen(syncer.syncAll())

    override fun deleteWallet(walletId: String) =
        localWalletDatastore.delete(walletId)
            .andThen(syncer.syncAll())

    override fun getWallets() = syncer.syncAll()
        .andThen(localWalletDatastore.getAll().firstOrError())
        .map { walletEntities ->
            walletEntities.map(walletMapper::mapFromEntity)
        }

    override fun setSelectedWallet(wallet: Wallet) = localWalletDatastore.setSelected(wallet.id)

    override fun getSelectedWallet() = localWalletDatastore.getSelected()
        .map(walletMapper::mapFromEntity)
}