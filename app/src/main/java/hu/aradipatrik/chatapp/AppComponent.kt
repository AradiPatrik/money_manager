package hu.aradipatrik.chatapp

import dagger.Component
import javax.inject.Singleton

@Component
@Singleton
interface AppComponent {
    val businessRunner: BusinessRunner
}