package com.aradipatrik.yamm

import android.app.Activity
import android.app.Application
import androidx.fragment.app.Fragment
import com.aradipatrik.yamm.di.AppComponent
import com.aradipatrik.yamm.di.DaggerAppComponent

@Suppress("unused")
class YammApplication : Application(), ComponentProvider {
    override val component: AppComponent by lazy {
        DaggerAppComponent.factory().create(this)
    }
}

interface ComponentProvider {
    val component: AppComponent
}

val Activity.injector
    get() =
        (application as ComponentProvider).component

val Fragment.injector get() = activity!!.injector
