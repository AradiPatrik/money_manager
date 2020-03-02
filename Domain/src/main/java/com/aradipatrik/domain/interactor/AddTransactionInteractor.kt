package com.aradipatrik.domain.interactor

import com.aradipatrik.domain.usecase.CompletableUseCase
import com.aradipatrik.domain.model.Transaction
import com.aradipatrik.domain.interfaces.data.TransactionRepository
import io.reactivex.Completable

class AddTransactionInteractor(
    private val transactionRepository: TransactionRepository
): CompletableUseCase<AddTransactionInteractor.Params> {
    data class Params(val transaction: Transaction) {
        companion object {
            fun forTransaction(transaction: Transaction): Params = Params(transaction)
        }
    }

    override fun get(params: Params?): Completable {
        require(params != null) { "${this::class.java.simpleName} params can't be null" }
        return transactionRepository.add(params.transaction)
    }
}
