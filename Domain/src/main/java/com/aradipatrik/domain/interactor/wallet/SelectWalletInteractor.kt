package com.aradipatrik.domain.interactor.wallet

import com.aradipatrik.domain.exceptions.wallet.WalletNotFoundException
import com.aradipatrik.domain.interfaces.data.WalletRepository
import com.aradipatrik.domain.holder.SelectedWalletHolder
import com.aradipatrik.domain.usecase.CompletableUseCase
import io.reactivex.Completable

class SelectWalletInteractor(
    private val selectedWalletHolder: SelectedWalletHolder,
    private val walletRepository: WalletRepository
) : CompletableUseCase<SelectWalletInteractor.Params> {
    data class Params internal constructor(val walletId: String) {
        companion object {
            fun forWallet(walletId: String) = Params(walletId)
        }
    }

    override fun get(params: Params?): Completable {
        require(params != null) { "Select wallet needs wallet id in order to work" }
        return walletRepository.getWallets()
            .doOnSuccess { wallets ->
                val wallet = wallets.firstOrNull { it.id == params.walletId }
                selectedWalletHolder.selectedWalletProcessor.offer(
                    wallet ?: throw WalletNotFoundException(wallets, params.walletId)
                )
            }
            .ignoreElement()
    }
}