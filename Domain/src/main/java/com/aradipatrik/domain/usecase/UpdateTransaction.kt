package com.aradipatrik.domain.usecase

import com.aradipatrik.domain.interactor.CompletableUseCase
import com.aradipatrik.domain.model.Transaction
import com.aradipatrik.domain.repository.TransactionRepository
import io.reactivex.Completable
import javax.inject.Inject

class UpdateTransaction @Inject constructor(
    private val transactionRepository: TransactionRepository
) : CompletableUseCase<UpdateTransaction.Params> {
    data class Params(val transaction: Transaction) {
        companion object {
            fun forTransaction(transaction: Transaction): Params = Params(transaction)
        }
    }

    override fun get(params: Params?): Completable {
        require(params != null) { "${this::class.java.simpleName} params can't be null" }
        return transactionRepository.update(params.transaction)
    }
}