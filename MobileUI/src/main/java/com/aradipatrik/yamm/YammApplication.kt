@file:Suppress("unused")

package com.aradipatrik.yamm

import android.app.Application
import com.aradipatrik.data.dataModule
import com.aradipatrik.domain.domainModule
import com.aradipatrik.local.database.localModule
import com.aradipatrik.local.database.rxPreferencesModule
import com.aradipatrik.presentation.presentationModule
import com.aradipatrik.remote.remoteModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

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
                    mobileUiModule,
                    rxPreferencesModule
                )
            )
        }
    }
}
