package com.aradipatrik.domain.interactor.transaction

import com.aradipatrik.domain.interfaces.data.TransactionRepository
import com.aradipatrik.domain.interfaces.data.WalletRepository
import com.aradipatrik.domain.model.Transaction
import com.aradipatrik.domain.usecase.ObservableUseCase
import io.reactivex.Observable
import org.joda.time.Interval

class GetTransactionsInIntervalInteractor(
    private val transferRepository: TransactionRepository,
    private val walletRepository: WalletRepository
) : ObservableUseCase<List<Transaction>, GetTransactionsInIntervalInteractor.Params> {
    data class Params(val interval: Interval) {
        companion object {
            fun forInterval(interval: Interval) = Params(interval)
        }
    }

    override fun get(params: Params?): Observable<List<Transaction>> {
        require(params != null) { "${this::class.java.simpleName} parameters can't be null" }
        return walletRepository.getSelectedWallet()
            .flatMapObservable { selectedWallet ->
                transferRepository.getInInterval(params.interval, selectedWallet.id)
            }
    }
}
