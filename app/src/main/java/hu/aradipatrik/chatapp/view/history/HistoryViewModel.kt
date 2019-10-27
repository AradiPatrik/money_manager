package hu.aradipatrik.chatapp.view.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import hu.aradipatrik.chatapp.model.Transaction
import hu.aradipatrik.chatapp.repository.TransactionRepository
import javax.inject.Inject

class HistoryViewModel @Inject constructor(private val repository: TransactionRepository): ViewModel() {
    val transactions = Transformations.map(repository.allTransactions) {

    }
}