package hu.aradipatrik.chatapp.di

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import hu.aradipatrik.chatapp.MainViewModel
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
}