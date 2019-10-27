package hu.aradipatrik.chatapp

import androidx.lifecycle.ViewModel
import hu.aradipatrik.chatapp.repository.TransactionRepository
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository
) : ViewModel() {
    fun foo() {
        // stub
    }
}
