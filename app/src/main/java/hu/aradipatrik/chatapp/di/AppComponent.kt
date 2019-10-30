package hu.aradipatrik.chatapp.di

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import hu.aradipatrik.chatapp.view.createtransaction.CreateTransactionViewModel
import hu.aradipatrik.chatapp.view.history.HistoryViewModel
import hu.aradipatrik.chatapp.view.main.MainViewModel
import javax.inject.Singleton

@Singleton
@Component(modules = [AssistedInjectModule::class])
interface AppComponent {

  @Component.Factory
  interface Factory {
    fun create(
      @BindsInstance applicationContext: Context
    ): AppComponent
  }

  val mainViewModel: MainViewModel
  val historyViewModel: HistoryViewModel
  val createTransactionViewModel: CreateTransactionViewModel
}
