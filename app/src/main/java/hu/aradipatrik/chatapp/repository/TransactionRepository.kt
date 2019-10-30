package hu.aradipatrik.chatapp.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import hu.aradipatrik.chatapp.model.Expense
import hu.aradipatrik.chatapp.model.ExpenseCategory
import hu.aradipatrik.chatapp.model.Income
import hu.aradipatrik.chatapp.model.IncomeCategory
import hu.aradipatrik.chatapp.model.Transaction
import org.joda.time.DateTime
import javax.inject.Inject
import kotlin.random.Random

val incomeCategories = listOf(
  ExpenseCategory("orange", "groceries", "Groceries"),
  ExpenseCategory("light_salmon", "gym", "Crossfit"),
  ExpenseCategory("dark_orange", "food", "Food"),
  ExpenseCategory("coral", "beauty", "Beauty"),
  ExpenseCategory("hot_pink", "social", "Social")
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
  DateTime(2019, 8, 8, 5, 5, 5),
  DateTime(2019, 8, 8, 6, 5, 5, 5),
  DateTime(2019, 10, 8, 6, 5, 7, 5),
  DateTime(2019, 10, 8, 6, 4, 5, 5),
  DateTime(2019, 10, 8, 5, 5, 5),
  DateTime(2019, 10, 7, 6, 5, 5, 5),
  DateTime(2019, 10, 7, 6, 5, 7, 5),
  DateTime(2019, 9, 7, 6, 4, 5, 5),
  DateTime(2019, 9, 6, 6, 4, 5, 5),
  DateTime(2019, 9, 6, 6, 4, 5, 5),
  DateTime(2019, 9, 5, 6, 4, 5, 5)
)

val incomeDays = listOf(
  DateTime(2019, 10, 9, 5, 5, 55),
  DateTime(2019, 9, 8, 5, 5, 53),
  DateTime(2019, 8, 7, 5, 5, 52)
)

class TransactionRepository @Inject constructor() {
  private val _allTransactions = MutableLiveData<List<Transaction>>()
  val allTransactions = _allTransactions

  init {
    _allTransactions.value = (0..10).map { transaction ->
      val amount = Random.nextInt(500, 2501)
      val category = incomeCategories[Random.nextInt(0, incomeCategories.size)]
      val memo = memos[Random.nextInt(0, memos.size)]
      Expense(transaction, amount, days[transaction], memo, category).also {
        Log.d(this::class.java.simpleName, it.toString())
      }
    }.plus((0..2).map {
      Income(
        11 + it,
        610_000,
        incomeDays[it],
        "Awww yiss!",
        IncomeCategory("", "wallet", "Fizetes")
      )
    })
  }
}
