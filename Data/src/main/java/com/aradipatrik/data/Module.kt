package com.aradipatrik.data

import com.aradipatrik.data.mapper.CategoryMapper
import com.aradipatrik.data.mapper.JoinedTransactionMapper
import com.aradipatrik.data.mapper.PartialTransactionMapper
import com.aradipatrik.data.mapper.WalletMapper
import com.aradipatrik.data.repository.CategoryRepositoryImpl
import com.aradipatrik.data.repository.ExpenseStatsRepositoryImpl
import com.aradipatrik.data.repository.SelectedMonthRepositoryImpl
import com.aradipatrik.data.repository.Syncer
import com.aradipatrik.data.repository.TransactionRepositoryImpl
import com.aradipatrik.data.repository.UserRepositoryImpl
import com.aradipatrik.data.repository.WalletRepositoryImpl
import com.aradipatrik.domain.interfaces.data.CategoryRepository
import com.aradipatrik.domain.interfaces.data.ExpenseStatsRepository
import com.aradipatrik.domain.interfaces.data.SelectedMonthRepository
import com.aradipatrik.domain.interfaces.data.TransactionRepository
import com.aradipatrik.domain.interfaces.data.UserRepository
import com.aradipatrik.domain.interfaces.data.WalletRepository
import org.koin.dsl.module

val dataModule = module {
    single<TransactionRepository> { TransactionRepositoryImpl(get(), get(), get(), get()) }
    single<CategoryRepository> { CategoryRepositoryImpl(get(), get(), get()) }
    single<UserRepository> { UserRepositoryImpl(get()) }
    single<WalletRepository> { WalletRepositoryImpl(get(), get(), get()) }
    single<SelectedMonthRepository> { SelectedMonthRepositoryImpl() }
    single<ExpenseStatsRepository> { ExpenseStatsRepositoryImpl(get()) }
    single { Syncer(get(), get(), get(), get(), get(), get(), get()) }
    single { JoinedTransactionMapper(get()) }
    single { PartialTransactionMapper() }
    single { CategoryMapper() }
    single { WalletMapper() }
}
