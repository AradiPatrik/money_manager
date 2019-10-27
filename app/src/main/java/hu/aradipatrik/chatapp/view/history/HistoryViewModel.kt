package hu.aradipatrik.chatapp.view.history

import androidx.lifecycle.ViewModel
import hu.aradipatrik.chatapp.repository.TransactionRepository
import javax.inject.Inject

class HistoryViewModel @Inject constructor(val repository: TransactionRepository): ViewModel() {

}