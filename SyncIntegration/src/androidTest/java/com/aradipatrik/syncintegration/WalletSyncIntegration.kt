package com.aradipatrik.syncintegration

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.aradipatrik.data.dataModule
import com.aradipatrik.domain.interfaces.data.UserRepository
import com.aradipatrik.domain.interfaces.data.WalletRepository
import com.aradipatrik.domain.model.User
import com.aradipatrik.domain.model.Wallet
import com.aradipatrik.local.database.TransactionDatabase
import com.aradipatrik.local.database.localModule
import com.aradipatrik.remote.remoteModule
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject

class WalletSyncIntegration : KoinTest {
    @get:Rule
    val instantTaskExecutionRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        startKoin {
            androidContext(InstrumentationRegistry.getInstrumentation().targetContext)
            modules(listOf(localModule, remoteModule, dataModule))
            loadKoinModules(module {
                single(override = true) {
                    Room.inMemoryDatabaseBuilder(
                        InstrumentationRegistry.getInstrumentation().context,
                        TransactionDatabase::class.java
                    )
                        .allowMainThreadQueries()
                        .build()
                }
            })
        }
    }

    private val walletRepository: WalletRepository by inject()
    private val userRepository: UserRepository by inject()

    @After
    fun teardown() {
        stopKoin()
    }

    @Test
    fun foo() {
        userRepository.setSignedInUser(User("testUserId")).blockingAwait()
        walletRepository.addWallet(Wallet("testWalletId", "wallet")).blockingAwait()
    }
}