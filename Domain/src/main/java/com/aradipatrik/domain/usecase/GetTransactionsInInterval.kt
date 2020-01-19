package com.aradipatrik.domain.usecase

import com.aradipatrik.domain.interactor.ObservableUseCase
import com.aradipatrik.domain.model.Transaction
import com.aradipatrik.domain.repository.TransactionRepository
import io.reactivex.Observable
import org.joda.time.Interval
import javax.inject.Inject

class GetTransactionsInInterval(
    private val transferRepository: TransactionRepository
): ObservableUseCase<List<Transaction>, GetTransactionsInInterval.Params> {
    data class Params(val interval: Interval) {
        companion object {
            fun forInterval(interval: Interval) = Params(interval)
        }
    }

    override fun get(params: Params?): Observable<List<Transaction>> {
        require(params != null) { "${this::class.java.simpleName} parameters can't be null" }
        return transferRepository.getInInterval(params.interval)
    }
}