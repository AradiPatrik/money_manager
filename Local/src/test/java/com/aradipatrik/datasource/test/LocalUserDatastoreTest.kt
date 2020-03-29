package com.aradipatrik.datasource.test

import android.preference.PreferenceManager
import androidx.core.content.SharedPreferencesCompat
import androidx.preference.Preference
import androidx.test.core.app.ApplicationProvider
import com.aradipatrik.data.datastore.user.LocalUserDatastore
import com.aradipatrik.domain.model.User
import com.aradipatrik.local.database.RxPreferencesUserDatastore
import com.f2prateek.rx.preferences2.RxSharedPreferences
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowSharedPreferences
import strikt.api.expectThat
import strikt.assertions.isEqualTo

@RunWith(RobolectricTestRunner::class)
class LocalUserDatastoreTest : KoinTest {
    val module = module {
        single {
            RxSharedPreferences.create(
                PreferenceManager.getDefaultSharedPreferences(
                    ApplicationProvider.getApplicationContext()
                )
            )
        }
        single<LocalUserDatastore> { RxPreferencesUserDatastore(get()) }
    }

    val localDatastore: LocalUserDatastore by inject()

    @Before
    fun setup() {
        startKoin { modules(module) }
    }

    @After
    fun teardown() {
        stopKoin()
    }

    @Test
    fun `getUser after saveUser should return correct user`() {
        localDatastore.setUser(User("testId")).blockingAwait()
        val result = localDatastore.getUser().blockingGet()
        expectThat(result).isEqualTo(User("testId"))
    }
}