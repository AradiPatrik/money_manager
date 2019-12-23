package com.aradipatrik.domain.usecase

import com.aradipatrik.domain.executor.PostExecutionThread
import com.aradipatrik.domain.interactor.ObservableUseCase
import com.aradipatrik.domain.model.Transaction
import com.aradipatrik.domain.repository.TransactionRepository
import io.reactivex.Observable
import org.joda.time.Interval
import javax.inject.Inject

class GetTransactionsInInterval @Inject constructor(
    private val transferRepository: TransactionRepository,
    postExecutionThread: PostExecutionThread
): ObservableUseCase<List<Transaction>, GetTransactionsInInterval.Params>(postExecutionThread) {
    data class Params(val interval: Interval) {
        companion object {
            fun forInterval(interval: Interval) = Params(interval)
        }
    }

    override fun buildUseCaseObservable(params: Params?): Observable<List<Transaction>> {
        require(params != null) { "${this::class.java.simpleName} parameters can't be null" }
        return transferRepository.getTransactionsInInterval(params.interval)
    }
}