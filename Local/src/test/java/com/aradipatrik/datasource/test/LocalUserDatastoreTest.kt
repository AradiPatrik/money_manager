package com.aradipatrik.datasource.test

import android.preference.PreferenceManager
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
import strikt.api.expectThat
import strikt.assertions.isEqualTo

@RunWith(RobolectricTestRunner::class)
class LocalUserDatastoreTest : BaseRoomTest() {
    private val localDatastore: LocalUserDatastore by inject()

    @Test
    fun `getUser after saveUser should return correct user`() {
        localDatastore.setUser(User("testId")).blockingAwait()
        val result = localDatastore.getUser().blockingGet()
        expectThat(result).isEqualTo(User("testId"))
    }

    @Test
    fun `isUserPresent should return false if user is not present`() {
        localDatastore.isUserPresent()
            .test()
            .assertValue(false)
    }

    @Test
    fun `isUserPresent should be true if user has been initialized`() {
        localDatastore.setUser(User("testId")).blockingAwait()
        localDatastore.isUserPresent()
            .test()
            .assertValue(true)
    }
}