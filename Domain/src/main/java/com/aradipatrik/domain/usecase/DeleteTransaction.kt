package com.aradipatrik.domain.usecase

import com.aradipatrik.domain.interactor.CompletableUseCase
import com.aradipatrik.domain.repository.TransactionRepository
import io.reactivex.Completable
import javax.inject.Inject

class DeleteTransaction(
    private val transactionRepository: TransactionRepository
) : CompletableUseCase<DeleteTransaction.Params> {
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