package hu.aradipatrik.chatapp.model

import org.joda.time.DateTime
import org.joda.time.Instant
import java.util.*

sealed class Transaction {
    abstract val amount: Int
    abstract val date: DateTime
    abstract val memo: String
    abstract val category: Category
}

data class Expense(
    override val amount: Int,
    override val date: DateTime,
    override val memo: String,
    override val category: ExpenseCategory
) : Transaction()

data class Income(
    override val amount: Int,
    override val date: DateTime,
    override val memo: String,
    override val category: IncomeCategory
) : Transaction()

sealed class Category {
    abstract val iconName: String
    abstract val name: String
}

data class ExpenseCategory(
    override val iconName: String,
    override val name: String
): Category()

data class IncomeCategory(
    override val iconName: String,
    override val name: String
): Category()
