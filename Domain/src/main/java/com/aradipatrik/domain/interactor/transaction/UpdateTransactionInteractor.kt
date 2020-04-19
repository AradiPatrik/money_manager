package com.aradipatrik.domain.interactor.transaction

import com.aradipatrik.domain.interfaces.data.TransactionRepository
import com.aradipatrik.domain.interfaces.data.WalletRepository
import com.aradipatrik.domain.model.Transaction
import com.aradipatrik.domain.usecase.CompletableUseCase
import io.reactivex.Completable

class UpdateTransactionInteractor(
    private val transactionRepository: TransactionRepository,
    private val walletRepository: WalletRepository
) : CompletableUseCase<UpdateTransactionInteractor.Params> {
    data class Params(val transaction: Transaction) {
        companion object {
            fun forTransaction(transaction: Transaction) = Params(transaction)
        }
    }

    override fun get(params: Params?): Completable {
        require(params != null) { "${this::class.java.simpleName} params can't be null" }
        return walletRepository.getSelectedWallet()
            .flatMapCompletable { selectedWallet ->
                transactionRepository.update(params.transaction, selectedWallet.id)
            }
    }
}
