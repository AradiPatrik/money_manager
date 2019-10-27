package hu.aradipatrik.chatapp.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import hu.aradipatrik.chatapp.R
import hu.aradipatrik.chatapp.model.Income
import hu.aradipatrik.chatapp.model.IncomeCategory
import org.joda.time.DateTime

import javax.inject.Inject
import kotlin.random.Random

val incomeCategories = listOf(
    IncomeCategory("orange", "groceries", "Groceries"),
    IncomeCategory("light_salmon", "gym", "Crossfit"),
    IncomeCategory("dark_orange", "food", "Food"),
    IncomeCategory("coral", "beauty", "Beauty"),
    IncomeCategory("hot_pink", "social", "Social")
)

val memos = listOf(
    "Yumm",
    "This was epic",
    "ZaZa Pizza!!!",
    "Mark",
    "Birthday",
    "Eskuvo",
    "Telefon, yay :)",
    "Lakber, nay :("
)

val days = listOf(
    DateTime(2019, 12, 8, 5, 5, 5),
    DateTime(2019, 12, 8, 6, 5, 5, 5),
    DateTime(2019, 12, 8, 6, 5, 7, 5),
    DateTime(2019, 12, 8, 6, 4, 5, 5),
    DateTime(2019, 12, 8, 5, 5, 5),
    DateTime(2019, 12, 7, 6, 5, 5, 5),
    DateTime(2019, 12, 7, 6, 5, 7, 5),
    DateTime(2019, 12, 7, 6, 4, 5, 5),
    DateTime(2019, 12, 6, 6, 4, 5, 5),
    DateTime(2019, 12, 6, 6, 4, 5, 5),
    DateTime(2019, 12, 5, 6, 4, 5, 5)
)

class TransactionRepository @Inject constructor() {
    private val _expenses = MutableLiveData<List<Income>>()
    val expenses: LiveData<List<Income>> = _expenses
    val allTransactions = expenses

    init {
        _expenses.value = (0..10).map {
            val month = Random.nextInt(1, 13)
            val day = Random.nextInt(1, 29)
            val hour = Random.nextInt(1, 24)
            val minute = Random.nextInt(1, 60)
            val dateTime = DateTime(2019, month, day, hour, minute)
            val amount = Random.nextInt(500, 2501)
            val category = incomeCategories[Random.nextInt(0, incomeCategories.size)]
            val memo = memos[Random.nextInt(0, memos.size)]
            Income(it, amount, days[it], memo, category).also {
                Log.d(this::class.java.simpleName, "$it")
            }
        }
    }
}