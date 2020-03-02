package com.aradipatrik.remote

import com.aradipatrik.data.datasource.category.RemoteCategoryDatastore
import com.aradipatrik.data.datasource.transaction.RemoteTransactionDatastore
import com.aradipatrik.remote.data.FirestoreRemoteCategoryDatastore
import com.aradipatrik.remote.data.FirestoreRemoteTransactionDatastore
import com.aradipatrik.remote.payloadfactory.CategoryPayloadFactory
import com.aradipatrik.remote.payloadfactory.CategoryResponseConverter
import com.aradipatrik.remote.payloadfactory.TransactionPayloadFactory
import com.aradipatrik.remote.payloadfactory.TransactionResponseConverter
import org.koin.dsl.module

val remoteModule = module {
    single<RemoteTransactionDatastore> {
        FirestoreRemoteTransactionDatastore(
            TEST_USER_ID,
            get(),
            get()
        )
    }
    single<RemoteCategoryDatastore> {
        FirestoreRemoteCategoryDatastore(
            TEST_USER_ID,
            get(),
            get()
        )
    }
    single { TransactionPayloadFactory() }
    single { TransactionResponseConverter() }
    single { CategoryPayloadFactory() }
    single { CategoryResponseConverter() }
}