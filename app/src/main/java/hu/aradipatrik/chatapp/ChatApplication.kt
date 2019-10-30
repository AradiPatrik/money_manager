package hu.aradipatrik.chatapp

import android.app.Activity
import android.app.Application
import androidx.fragment.app.Fragment
import hu.aradipatrik.chatapp.di.AppComponent
import hu.aradipatrik.chatapp.di.DaggerAppComponent

@Suppress("unused")
class ChatApplication : Application(), ComponentProvider {
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
