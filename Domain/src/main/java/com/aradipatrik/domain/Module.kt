package com.aradipatrik.domain

import com.aradipatrik.domain.usecase.*
import org.koin.dsl.module

val domainModule = module {
    factory { GetTransactionsInInterval(get()) }
    factory { AddTransaction(get()) }
    factory { UpdateTransaction(get()) }
    factory { DeleteTransaction(get()) }
    factory { GetCategories(get()) }
}