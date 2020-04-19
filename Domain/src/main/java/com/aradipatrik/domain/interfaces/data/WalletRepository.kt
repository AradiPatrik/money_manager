package com.aradipatrik.domain.interfaces.data

import com.aradipatrik.domain.model.Wallet
import io.reactivex.Completable
import io.reactivex.Single

interface WalletRepository {
    fun addWallet(wallet: Wallet): Completable
    fun updateWallet(wallet: Wallet): Completable
    fun deleteWallet(walletId: String): Completable
    fun getWallets(): Single<List<Wallet>>
    fun setSelectedWallet(wallet: Wallet): Completable
    fun getSelectedWallet(): Single<Wallet>
}
