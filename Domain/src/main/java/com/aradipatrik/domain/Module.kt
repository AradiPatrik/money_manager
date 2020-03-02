package com.aradipatrik.domain


import com.aradipatrik.domain.interactor.AddTransactionInteractor
import com.aradipatrik.domain.interactor.DeleteTransactionInteractor
import com.aradipatrik.domain.interactor.GetCategoriesInteractor
import com.aradipatrik.domain.interactor.GetTransactionsInIntervalInteractor
import com.aradipatrik.domain.interactor.UpdateTransactionInteractor
import org.koin.dsl.module

val domainModule = module {
    factory { GetTransactionsInIntervalInteractor(get()) }
    factory { AddTransactionInteractor(get()) }
    factory { UpdateTransactionInteractor(get()) }
    factory { DeleteTransactionInteractor(get()) }
    factory { GetCategoriesInteractor(get(), get()) }
}
