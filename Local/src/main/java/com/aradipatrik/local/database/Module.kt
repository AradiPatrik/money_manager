package com.aradipatrik.local.database

import androidx.preference.PreferenceManager
import com.aradipatrik.data.datastore.category.LocalCategoryDatastore
import com.aradipatrik.data.datastore.transaction.LocalTransactionDatastore
import com.aradipatrik.data.datastore.user.LocalUserDatastore
import com.aradipatrik.data.datastore.wallet.LocalWalletDatastore
import com.aradipatrik.local.database.mapper.CategoryRowMapper
import com.aradipatrik.local.database.mapper.TransactionRowMapper
import com.aradipatrik.local.database.mapper.WalletRowMapper
import com.f2prateek.rx.preferences2.RxSharedPreferences
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val localModule = module {
    single<LocalTransactionDatastore> {
        RoomLocalTransactionDatastore(get(), get())
    }
    single<LocalCategoryDatastore> {
        RoomLocalCategoryDatastore(get(), get())
    }
    single<LocalWalletDatastore> {
        RoomLocalWalletDatastore(get(), get())
    }
    single<LocalUserDatastore> {
        RxPreferencesUserDatastore(
            RxSharedPreferences.create(
                PreferenceManager.getDefaultSharedPreferences(androidContext())
            )
        )
    }
    single { TransactionDatabase.getInstance(androidContext()) }
    single { get<TransactionDatabase>().transactionDao() }
    single { get<TransactionDatabase>().categoryDao() }
    single { get<TransactionDatabase>().walletDao() }
    single { CategoryRowMapper() }
    single { TransactionRowMapper(get()) }
    single { WalletRowMapper() }
}
