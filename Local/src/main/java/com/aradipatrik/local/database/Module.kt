package com.aradipatrik.local.database

import com.aradipatrik.data.datasource.category.LocalCategoryDatastore
import com.aradipatrik.data.datasource.transaction.LocalTransactionDatastore
import com.aradipatrik.local.database.mapper.CategoryRowMapper
import com.aradipatrik.local.database.mapper.TransactionRowMapper
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val localModule = module {
    single<LocalTransactionDatastore> {
        RoomLocalTransactionDatastore(get(), get())
    }
    single<LocalCategoryDatastore> {
        RoomLocalCategoryDatastore(get(), get())
    }
    single { TransactionDatabase.getInstance(androidContext()) }
    single { get<TransactionDatabase>().transactionDao() }
    single { get<TransactionDatabase>().categoryDao() }
    single { CategoryRowMapper() }
    single { TransactionRowMapper(get()) }
}