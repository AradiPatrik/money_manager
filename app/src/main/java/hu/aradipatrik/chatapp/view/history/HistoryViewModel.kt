package hu.aradipatrik.chatapp.view.history

import android.util.Log
import androidx.lifecycle.*
import hu.aradipatrik.chatapp.model.Expense
import hu.aradipatrik.chatapp.model.Income
import hu.aradipatrik.chatapp.model.Transaction
import hu.aradipatrik.chatapp.repository.TransactionRepository
import org.joda.time.DateTime
import org.joda.time.Period
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.Comparator

@Singleton
class HistoryViewModel @Inject constructor(private val repository: TransactionRepository) :
    ViewModel() {
    private var lastAllHistoryResult: TreeMap<DateTime, TreeSet<Transaction>>? = null

    private val _allHistoryItems = Transformations.map(repository.allTransactions) { transactions ->
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

    private val _historyItemsThisMonth = MediatorLiveData<Map<DateTime, Set<Transaction>>>()
    val historyItemsThisMonth: LiveData<Map<DateTime, Set<Transaction>>> =
        _historyItemsThisMonth

    private val _selectedDate = MutableLiveData(DateTime.now())
    val selectedDate: LiveData<DateTime> = _selectedDate

    val monthlySaving = Transformations.map(_historyItemsThisMonth) { dateToTransactions ->
        dateToTransactions.values.sumBy {  transactionsOfDate ->
            transactionsOfDate.sumBy {
                if (it is Income) {
                    it.amount
                } else {
                    -it.amount
                }
            }
        }
    }

    val monthlyIncome = Transformations.map(_historyItemsThisMonth) { dateToTransactions ->
        dateToTransactions.values.sumBy { transactionsOfDate ->
            transactionsOfDate.sumBy {
                if (it is Income) {
                    it.amount
                } else {
                    0
                }
            }
        }
    }

    val monthlyExpense = Transformations.map(_historyItemsThisMonth) { dateToTransactions ->
        dateToTransactions.values.sumBy { transactionsOfDate ->
            transactionsOfDate.sumBy {
                if (it is Expense) {
                    it.amount
                } else {
                    0
                }
            }
        }
    }

    val totalTillSelectedMonth = Transformations.map(repository._allTransactions) {

    }

    init {
        _historyItemsThisMonth.addSource(_allHistoryItems) { monthsToTransactions ->
            lastAllHistoryResult = monthsToTransactions
            _historyItemsThisMonth.value = filterHistoryItems(monthsToTransactions)
        }
    }

    private fun filterHistoryItems(monthsToTransactions: TreeMap<DateTime, TreeSet<Transaction>>) =
        monthsToTransactions.filterKeys {
            it.monthOfYear == _selectedDate.value?.monthOfYear &&
                    it.year == _selectedDate.value?.year
        }

    fun nextMonth() {
        _selectedDate.value = _selectedDate.value?.plus(Period.months(1))
        updateMonthFilter()
    }

    fun previousMonth() {
        _selectedDate.value = _selectedDate.value?.minus(Period.months(1))
        updateMonthFilter()
    }

    private fun updateMonthFilter() {
        _allHistoryItems.value?.let {
            _historyItemsThisMonth.value = filterHistoryItems(it)
        }
    }
}