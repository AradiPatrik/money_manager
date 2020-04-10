package com.aradipatrik.domain.interactor.transaction

import com.aradipatrik.domain.interfaces.data.TransactionRepository
import com.aradipatrik.domain.model.Transaction
import com.aradipatrik.domain.usecase.CompletableUseCase
import io.reactivex.Completable

class UpdateTransactionInteractor(
    private val transactionRepository: TransactionRepository
) : CompletableUseCase<UpdateTransactionInteractor.Params> {
    data class Params(val transaction: Transaction) {
        companion object {
            fun forTransaction(transaction: Transaction): Params =
                Params(
                    transaction
                )
        }
    }

    override fun get(params: Params?): Completable {
        require(params != null) { "${this::class.java.simpleName} params can't be null" }
        return transactionRepository.update(params.transaction)
    }
}