package com.aradipatrik.datasource.test

import android.preference.PreferenceManager
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.aradipatrik.local.database.TransactionDatabase
import com.f2prateek.rx.preferences2.RxSharedPreferences

import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject

abstract class BaseRoomTest: KoinTest {
    @get:Rule
    val instantTaskExecutionRule = InstantTaskExecutorRule()

    val database: TransactionDatabase by inject()

    @Before
    fun setup() {
        startKoin {
            modules(listOf(com.aradipatrik.local.database.localModule, module {
                single(override = true) {
                    Room.inMemoryDatabaseBuilder(
                        ApplicationProvider.getApplicationContext(),
                        TransactionDatabase::class.java
                    )
                        .allowMainThreadQueries()
                        .build()
                }
                single {
                    RxSharedPreferences.create(
                        PreferenceManager.getDefaultSharedPreferences(
                            ApplicationProvider.getApplicationContext()
                        )
                    )
                }
            }))
        }
    }

    @After
    fun teardown() {
        database.close()
        stopKoin()
    }
}