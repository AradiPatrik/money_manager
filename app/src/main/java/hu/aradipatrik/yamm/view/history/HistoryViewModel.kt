package hu.aradipatrik.yamm.view.history

import androidx.lifecycle.*
import hu.aradipatrik.yamm.model.Expense
import hu.aradipatrik.yamm.model.Income
import hu.aradipatrik.yamm.model.Transaction
import hu.aradipatrik.yamm.repository.TransactionRepository
import org.joda.time.DateTime
import org.joda.time.Period
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.Comparator
import kotlin.collections.set

@Singleton
class HistoryViewModel @Inject constructor(
        private val repository: TransactionRepository
) : ViewModel() {
    private val _allHistoryItems =
            Transformations.map(repository.allTransactions) { transactions ->
                val dayComparator = Comparator<DateTime> { lhs, rhs ->
                    rhs.withTimeAtStartOfDay().compareTo(lhs.withTimeAtStartOfDay())
                }
                val transactionTimeComparator = Comparator<Transaction> { lhs, rhs ->
                    rhs.date.compareTo(lhs.date)
                }
                val datesToTransactions =
                        TreeMap<DateTime, TreeSet<Transaction>>(dayComparator)
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

    private val _historyItemsThisMonth =
            MediatorLiveData<Map<DateTime, Set<Transaction>>>()
    val historyItemsThisMonth: LiveData<Map<DateTime, Set<Transaction>>> =
            _historyItemsThisMonth

    private val _selectedDate = MutableLiveData(DateTime.now())
    val selectedMonth = Transformations.map(_selectedDate) { it.toString("MMMM") }
    val selectedYear = Transformations.map(_selectedDate) { it.toString("YYYY") }

    val monthlySaving =
            Transformations.map(_historyItemsThisMonth) { dateToTransactions ->
                dateToTransactions.values.sumBy { transactionsOfDate ->
                    transactionsOfDate.sumBy {
                        if (it is Income) {
                            it.amount
                        } else {
                            -it.amount
                        }
                    }
                }.toString()
            }

    val monthlyIncome =
            Transformations.map(_historyItemsThisMonth) { dateToTransactions ->
                dateToTransactions.values.sumBy { transactionsOfDate ->
                    transactionsOfDate.sumBy {
                        if (it is Income) {
                            it.amount
                        } else {
                            0
                        }
                    }
                }.toString()
            }

    val monthlyExpense =
            Transformations.map(_historyItemsThisMonth) { dateToTransactions ->
                dateToTransactions.values.sumBy { transactionsOfDate ->
                    transactionsOfDate.sumBy {
                        if (it is Expense) {
                            it.amount
                        } else {
                            0
                        }
                    }
                }.toString()
            }

    private val _totalTillSelectedMonth = MediatorLiveData<Int>()
    val totalTillSelectedMonth: LiveData<String> =
            Transformations.map(_totalTillSelectedMonth) { it.toString() }

    private fun calculateTotalTillSelectedMonth(transactions: List<Transaction>) =
            _selectedDate.value?.let { selectedDate ->
                transactions.filter {
                    it.date.isBefore(
                            selectedDate.dayOfMonth()
                                    .withMaximumValue()
                                    .withHourOfDay(23)
                                    .withMinuteOfHour(59)
                                    .withSecondOfMinute(59)
                    )
                }.sumBy {
                    if (it is Income) {
                        it.amount
                    } else {
                        -it.amount
                    }
                }
            }

    init {
        _historyItemsThisMonth.addSource(_allHistoryItems) { monthsToTransactions ->
            _historyItemsThisMonth.value = filterHistoryItems(monthsToTransactions)
        }
        _totalTillSelectedMonth.addSource(repository.allTransactions) { transactions ->
            _totalTillSelectedMonth.value =
                    calculateTotalTillSelectedMonth(transactions)
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
        val allTransactions = repository.allTransactions.value
        require(allTransactions != null) { "Transactinos were null when updating list" }
        _totalTillSelectedMonth.value =
                calculateTotalTillSelectedMonth(allTransactions)
    }

    fun previousMonth() {
        _selectedDate.value = _selectedDate.value?.minus(Period.months(1))
        updateMonthFilter()
        val allTransactions = repository.allTransactions.value
        require(allTransactions != null) { "Transactinos were null when updating list" }
        _totalTillSelectedMonth.value =
                calculateTotalTillSelectedMonth(allTransactions)
    }

    private fun updateMonthFilter() {
        _allHistoryItems.value?.let {
            _historyItemsThisMonth.value = filterHistoryItems(it)
        }
    }
}
