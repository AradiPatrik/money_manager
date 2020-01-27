@file:Suppress("unused")

package com.aradipatrik.yamm

import android.app.Application
import com.aradipatrik.data.dataModule
import com.aradipatrik.data.datasource.category.RemoteCategoryDatastore
import com.aradipatrik.data.datasource.transaction.RemoteTransactionDatastore
import com.aradipatrik.data.mapper.CategoryMapper
import com.aradipatrik.data.mapper.JoinedTransactionMapper
import com.aradipatrik.data.mapper.PartialTransactionMapper
import com.aradipatrik.data.repository.CategoryRepositoryImpl
import com.aradipatrik.data.repository.Syncer
import com.aradipatrik.data.repository.TransactionRepositoryImpl
import com.aradipatrik.domain.domainModule
import com.aradipatrik.domain.repository.CategoryRepository
import com.aradipatrik.domain.repository.TransactionRepository
import com.aradipatrik.domain.usecase.*
import com.aradipatrik.local.database.localModule
import com.aradipatrik.presentation.mapper.CategoryPresentationMapper
import com.aradipatrik.presentation.mapper.TransactionPresentationMapper
import com.aradipatrik.presentation.presentationModule
import com.aradipatrik.remote.FirestoreRemoteCategoryDatastore
import com.aradipatrik.remote.FirestoreRemoteTransactionDatastore
import com.aradipatrik.remote.TEST_USER_ID
import com.aradipatrik.remote.payloadfactory.CategoryPayloadFactory
import com.aradipatrik.remote.payloadfactory.CategoryResponseConverter
import com.aradipatrik.remote.payloadfactory.TransactionPayloadFactory
import com.aradipatrik.remote.payloadfactory.TransactionResponseConverter
import com.aradipatrik.remote.remoteModule
import com.aradipatrik.yamm.common.mapper.IconMapper
import com.aradipatrik.yamm.features.add.transaction.adapter.CategoryAdapter
import com.aradipatrik.yamm.features.history.adapter.HistoryAdapter
import com.aradipatrik.yamm.features.history.mapper.TransactionViewDataMapper
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.dsl.module

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
