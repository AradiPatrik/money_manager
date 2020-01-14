package com.aradipatrik.presentation.injection

import android.app.Application
import com.aradipatrik.domain.usecase.AddTransaction
import com.aradipatrik.domain.usecase.DeleteTransaction
import com.aradipatrik.domain.usecase.GetTransactionsInInterval
import com.aradipatrik.domain.usecase.UpdateTransaction
import com.aradipatrik.presentation.mapper.TransactionPresentationMapper

interface UseCaseContainer {
    val getTransactionsInInterval: GetTransactionsInInterval
    val addTransaction: AddTransaction
    val deleteTransaction: DeleteTransaction
    val updateTransaction: UpdateTransaction
}

interface MapperContainer {
    val mapper: TransactionPresentationMapper
}

abstract class DaggerApplication : Application() {
    abstract val useCaseContainer: UseCaseContainer
    abstract val mapperContainer: MapperContainer
}