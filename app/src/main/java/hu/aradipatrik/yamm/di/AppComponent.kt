package hu.aradipatrik.yamm.di

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import hu.aradipatrik.yamm.view.categoryselect.CategorySelectViewModel
import hu.aradipatrik.yamm.view.history.HistoryViewModel
import hu.aradipatrik.yamm.view.main.MainViewModel
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
    val categorySelectViewModel: CategorySelectViewModel
}
