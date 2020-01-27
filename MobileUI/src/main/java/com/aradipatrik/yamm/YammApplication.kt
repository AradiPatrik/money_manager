@file:Suppress("unused")

package com.aradipatrik.yamm

import android.app.Application
import com.aradipatrik.data.datasource.category.RemoteCategoryDatastore
import com.aradipatrik.data.datasource.transaction.RemoteTransactionDatastore
import com.aradipatrik.data.mapper.CategoryMapper
import com.aradipatrik.data.mapper.JoinedTransactionMapper
import com.aradipatrik.data.mapper.PartialTransactionMapper
import com.aradipatrik.data.repository.CategoryRepositoryImpl
import com.aradipatrik.data.repository.Syncer
import com.aradipatrik.data.repository.TransactionRepositoryImpl
import com.aradipatrik.domain.repository.CategoryRepository
import com.aradipatrik.domain.repository.TransactionRepository
import com.aradipatrik.domain.usecase.*
import com.aradipatrik.local.database.localModule
import com.aradipatrik.presentation.mapper.CategoryPresentationMapper
import com.aradipatrik.presentation.mapper.TransactionPresentationMapper
import com.aradipatrik.remote.FirestoreRemoteCategoryDatastore
import com.aradipatrik.remote.FirestoreRemoteTransactionDatastore
import com.aradipatrik.remote.TEST_USER_ID
import com.aradipatrik.remote.payloadfactory.CategoryPayloadFactory
import com.aradipatrik.remote.payloadfactory.CategoryResponseConverter
import com.aradipatrik.remote.payloadfactory.TransactionPayloadFactory
import com.aradipatrik.remote.payloadfactory.TransactionResponseConverter
import com.aradipatrik.yamm.common.mapper.IconMapper
import com.aradipatrik.yamm.features.add.transaction.adapter.CategoryAdapter
import com.aradipatrik.yamm.features.history.adapter.HistoryAdapter
import com.aradipatrik.yamm.features.history.mapper.TransactionViewDataMapper
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.dsl.module

val presentationModule = module {
    single { CategoryPresentationMapper() }
    single { TransactionPresentationMapper(get()) }
}

val domainModule = module {
    factory { GetTransactionsInInterval(get()) }
    factory { AddTransaction(get()) }
    factory { UpdateTransaction(get()) }
    factory { DeleteTransaction(get()) }
    factory { GetCategories(get()) }
}

val dataModule = module {
    single<TransactionRepository> { TransactionRepositoryImpl(get(), get(), get(), get()) }
    single<CategoryRepository> { CategoryRepositoryImpl(get(), get(), get()) }
    single { Syncer(get(), get(), get(), get()) }
    single { JoinedTransactionMapper(get()) }
    single { PartialTransactionMapper() }
    single { CategoryMapper() }
}

val remoteModule = module {
    single<RemoteTransactionDatastore> {
        FirestoreRemoteTransactionDatastore(TEST_USER_ID, get(), get())
    }
    single<RemoteCategoryDatastore> {
        FirestoreRemoteCategoryDatastore(TEST_USER_ID, get(), get())
    }
    single { TransactionPayloadFactory() }
    single { TransactionResponseConverter() }
    single { CategoryPayloadFactory() }
    single { CategoryResponseConverter() }
}

val mobileUiModule = module {
    factory { HistoryAdapter() }
    factory { CategoryAdapter() }
    single { TransactionViewDataMapper(get()) }
    single { IconMapper() }
}

class YammApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@YammApplication)
            modules(
                listOf(
                    presentationModule,
                    domainModule,
                    dataModule,
                    remoteModule,
                    localModule,
                    mobileUiModule
                )
            )
        }
    }
}
