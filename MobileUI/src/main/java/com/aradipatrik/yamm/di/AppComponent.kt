package com.aradipatrik.yamm.di

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import com.aradipatrik.yamm.view.categoryselect.CategorySelectViewModel
import com.aradipatrik.yamm.view.history.HistoryViewModel
import com.aradipatrik.yamm.view.main.MainViewModel
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
