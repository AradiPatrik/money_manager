package com.aradipatrik.data

import com.aradipatrik.data.mapper.CategoryMapper
import com.aradipatrik.data.mapper.JoinedTransactionMapper
import com.aradipatrik.data.mapper.PartialTransactionMapper
import com.aradipatrik.data.repository.CategoryRepositoryImpl
import com.aradipatrik.data.repository.Syncer
import com.aradipatrik.data.repository.TransactionRepositoryImpl
import com.aradipatrik.domain.interfaces.CategoryRepository
import com.aradipatrik.domain.interfaces.TransactionRepository
import org.koin.dsl.module

val dataModule = module {
    single<TransactionRepository> { TransactionRepositoryImpl(get(), get(), get(), get()) }
    single<CategoryRepository> { CategoryRepositoryImpl(get(), get(), get()) }
    single { Syncer(get(), get(), get(), get()) }
    single { JoinedTransactionMapper(get()) }
    single { PartialTransactionMapper() }
    single { CategoryMapper() }
}
