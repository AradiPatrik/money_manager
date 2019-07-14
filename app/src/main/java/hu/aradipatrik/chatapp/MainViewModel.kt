package hu.aradipatrik.chatapp

import androidx.lifecycle.ViewModel
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val businessRunner: BusinessRunner
) : ViewModel() {
    fun runBusiness() {
        businessRunner.runBusiness()
    }
}
