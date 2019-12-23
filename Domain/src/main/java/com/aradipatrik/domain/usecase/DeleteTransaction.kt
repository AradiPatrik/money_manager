package com.aradipatrik.domain.usecase

import com.aradipatrik.domain.executor.PostExecutionThread
import com.aradipatrik.domain.interactor.CompletableUseCase
import com.aradipatrik.domain.repository.TransactionRepository
import io.reactivex.Completable
import javax.inject.Inject

class DeleteTransaction @Inject constructor(
    private val transactionRepository: TransactionRepository,
    postExecutionThread: PostExecutionThread
) : CompletableUseCase<DeleteTransaction.Params>(postExecutionThread) {
    data class Params(val transactionId: String) {
        companion object {
            fun forTransaction(transactionId: String) = Params(transactionId)
        }
    }

    override fun buildUseCaseCompletable(params: Params?): Completable {
        require(params != null) { "${this::class.java.simpleName} params can't be null" }
        return transactionRepository.deleteTransaction(params.transactionId)
    }
}