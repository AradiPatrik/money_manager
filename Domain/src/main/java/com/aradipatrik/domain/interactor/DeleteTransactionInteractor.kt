package com.aradipatrik.domain.interactor

import com.aradipatrik.domain.usecase.CompletableUseCase
import com.aradipatrik.domain.interfaces.TransactionRepository
import io.reactivex.Completable

class DeleteTransactionInteractor(
    private val transactionRepository: TransactionRepository
) : CompletableUseCase<DeleteTransactionInteractor.Params> {
    data class Params(val transactionId: String) {
        companion object {
            fun forTransaction(transactionId: String) = Params(transactionId)
        }
    }

    override fun get(params: Params?): Completable {
        require(params != null) { "${this::class.java.simpleName} params can't be null" }
        return transactionRepository.delete(params.transactionId)
    }
}
