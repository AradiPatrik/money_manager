package hu.aradipatrik.chatapp.view.history

import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import hu.aradipatrik.chatapp.R
import hu.aradipatrik.chatapp.model.Transaction
import hu.aradipatrik.chatapp.repository.TransactionRepository
import hu.aradipatrik.chatapp.view.history.HistoryItem.HeaderItem
import hu.aradipatrik.chatapp.view.history.HistoryItem.TransactionItem
import org.joda.time.DateTime
import java.util.*
import javax.inject.Inject
import kotlin.Comparator

class HistoryViewModel @Inject constructor(private val repository: TransactionRepository) :
    ViewModel() {
    val historyItems = Transformations.map(repository.allTransactions) { transactions ->
        val dayComparator = Comparator<DateTime> { lhs, rhs ->
            rhs.withTimeAtStartOfDay().compareTo(lhs.withTimeAtStartOfDay())
        }
        val transactionTimeComparator = Comparator<Transaction> { lhs, rhs ->
            rhs.date.compareTo(lhs.date)
        }
        val datesToTransactions = TreeMap<DateTime, TreeSet<Transaction>>(dayComparator)
        transactions.forEach {
            if (datesToTransactions.containsKey(it.date)) {
                val transactionSet = datesToTransactions[it.date]
                require(transactionSet != null)
                transactionSet.add(it)
            } else {
                datesToTransactions[it.date] = TreeSet(transactionTimeComparator)
                val transactionSet = datesToTransactions[it.date]
                require(transactionSet != null)
                transactionSet.add(it)
            }
        }
        datesToTransactions
    }

}