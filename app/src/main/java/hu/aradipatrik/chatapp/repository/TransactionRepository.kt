package hu.aradipatrik.chatapp.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import hu.aradipatrik.chatapp.model.Income
import hu.aradipatrik.chatapp.model.IncomeCategory
import org.joda.time.DateTime

import javax.inject.Inject
import kotlin.random.Random

val incomeCategories = listOf(
    IncomeCategory("", "Groceries"),
    IncomeCategory("", "Crossfit"),
    IncomeCategory("", "Food"),
    IncomeCategory("", "Beauty"),
    IncomeCategory("", "Social")
)

class TransactionRepository @Inject constructor() {
    private val _expenses = MutableLiveData<List<Income>>()
    val expenses: LiveData<List<Income>> = _expenses

    init {
        _expenses.value = (0..10).map {
            val month = Random.nextInt(1, 13)
            val day = Random.nextInt(1, 29)
            val hour = Random.nextInt(1, 24)
            val minute = Random.nextInt(1, 60)
            val dateTime = DateTime(2019, month, day, hour, minute)
            val amount = Random.nextInt(500 ,2501)
            val category = incomeCategories[Random.nextInt(0, incomeCategories.size)]
            Income(amount, dateTime, "", category).also {
                Log.d(this::class.java.simpleName, "$it")
            }
        }
    }
}