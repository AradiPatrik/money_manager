package com.aradipatrik.domain.interactor.wallet

import com.aradipatrik.domain.interfaces.data.WalletRepository
import com.aradipatrik.domain.model.Wallet
import com.aradipatrik.domain.usecase.CompletableUseCase

class SelectFirstWalletInteractor(
    private val walletRepository: WalletRepository
) : CompletableUseCase<Unit> {
    override fun get(params: Unit?) = walletRepository
        .getWallets()
        .map(List<Wallet>::first)
        .flatMapCompletable(walletRepository::setSelectedWallet)
}
