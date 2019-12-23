package com.aradipatrik.domain.usecase

import com.aradipatrik.domain.executor.PostExecutionThread
import com.aradipatrik.domain.interactor.ObservableUseCase
import com.aradipatrik.domain.model.Transaction
import com.aradipatrik.domain.repository.TransactionRepository
import javax.inject.Inject

class GetTransactions @Inject constructor(
    private val transactionRepository: TransactionRepository,
    postExecutionThread: PostExecutionThread
) : ObservableUseCase<List<Transaction>, Nothing>(postExecutionThread) {
    override fun buildUseCaseObservable(params: Nothing?) =
        transactionRepository.getAllTransactions()
}