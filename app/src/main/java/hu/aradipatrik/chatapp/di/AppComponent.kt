package hu.aradipatrik.chatapp

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Component
@Singleton
interface AppComponent {

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance applicationContext: Context
        ): AppComponent
    }

    val mainViewModel: MainViewModel
}