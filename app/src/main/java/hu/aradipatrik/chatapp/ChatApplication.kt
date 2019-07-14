package hu.aradipatrik.chatapp

import android.app.Activity
import android.app.Application
import hu.aradipatrik.chatapp.di.AppComponent
import hu.aradipatrik.chatapp.di.DaggerAppComponent

class ChatApplication : Application(), ComponentProvider {
    override val component: AppComponent by lazy {
        DaggerAppComponent.factory().create(this)
    }
}

interface ComponentProvider {
    val component: AppComponent
}

val Activity.injector get() =
    (application as ComponentProvider).component
