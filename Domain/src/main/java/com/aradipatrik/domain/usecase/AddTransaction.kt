package com.aradipatrik.domain.usecase

import com.aradipatrik.domain.executor.PostExecutionThread
import com.aradipatrik.domain.interactor.CompletableUseCase
import com.aradipatrik.domain.model.Transaction
import com.aradipatrik.domain.repository.TransactionRepository
import io.reactivex.Completable
import javax.inject.Inject

class AddTransaction @Inject constructor(
    private val transactionRepository: TransactionRepository,
    postExecutionThread: PostExecutionThread
): CompletableUseCase<AddTransaction.Params>(postExecutionThread) {
    data class Params(val transaction: Transaction) {
        companion object {
            fun forTransaction(transaction: Transaction): Params = Params(transaction)
        }
    }

    override fun buildUseCaseCompletable(params: Params?): Completable {
        require(params != null) { "${this::class.java.simpleName} params can't be null"}
        return transactionRepository.addTransaction(params.transaction)
    }
}