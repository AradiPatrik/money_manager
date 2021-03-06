package com.aradipatrik.remote

import com.aradipatrik.data.datastore.category.RemoteCategoryDatastore
import com.aradipatrik.data.datastore.transaction.RemoteTransactionDatastore
import com.aradipatrik.data.datastore.wallet.RemoteWalletDatastore
import com.aradipatrik.domain.interfaces.auth.Authenticator
import com.aradipatrik.remote.auth.FirebaseAuthenticator
import com.aradipatrik.remote.data.FirestoreRemoteCategoryDatastore
import com.aradipatrik.remote.data.FirestoreRemoteTransactionDatastore
import com.aradipatrik.remote.data.FirestoreRemoteWalletDatastore
import com.aradipatrik.remote.mapper.FirebaseErrorMapper
import com.aradipatrik.remote.mapper.FirebaseUserMapper
import com.aradipatrik.remote.payloadfactory.CategoryPayloadFactory
import com.aradipatrik.remote.payloadfactory.CategoryResponseConverter
import com.aradipatrik.remote.payloadfactory.TransactionPayloadFactory
import com.aradipatrik.remote.payloadfactory.TransactionResponseConverter
import com.aradipatrik.remote.payloadfactory.WalletPayloadFactory
import com.aradipatrik.remote.payloadfactory.WalletResponseConverter
import org.koin.dsl.module

val remoteModule = module {
    single<RemoteTransactionDatastore> {
        FirestoreRemoteTransactionDatastore(get(), get())
    }
    single<RemoteCategoryDatastore> {
        FirestoreRemoteCategoryDatastore(get(), get())
    }
    single<Authenticator> {
        FirebaseAuthenticator(get(), get())
    }
    single<RemoteWalletDatastore> {
        FirestoreRemoteWalletDatastore(get(), get())
    }
    single { TransactionPayloadFactory() }
    single { TransactionResponseConverter() }
    single { CategoryPayloadFactory() }
    single { CategoryResponseConverter() }
    single { WalletPayloadFactory() }
    single { WalletResponseConverter() }
    single { FirebaseErrorMapper() }
    single { FirebaseUserMapper() }
}
