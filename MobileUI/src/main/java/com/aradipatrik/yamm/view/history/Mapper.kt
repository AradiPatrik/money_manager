package com.aradipatrik.yamm.view.history

import com.aradipatrik.yamm.R
import com.aradipatrik.yamm.model.Income
import com.aradipatrik.yamm.model.Transaction
import org.joda.time.DateTime
import java.util.*

fun mapCategorisedTransactionsToItems(
        categorisedTransactions: Map<DateTime, Set<Transaction>>
): List<HistoryItem> = categorisedTransactions.flatMap { (date, transactions) ->
    val historyItems = ArrayList<HistoryItem>()

    historyItems.add(HistoryItem.HeaderItem(date))
    historyItems.addAll(transactions.map {
        HistoryItem.TransactionItem(
                it.uid,
                mapIconNameToIconResId(it.category.iconName),
                mapColorNameToColorResId(it.category.colorName),
                it.category.name,
                it.memo,
                if (it is Income) {
                    it.amount
                } else {
                    -it.amount
                }
        )
    })

    historyItems
}

fun mapColorNameToColorResId(name: String) = when (name) {
    "orange" -> R.color.mat1
    "light_salmon" -> R.color.mat4
    "dark_orange" -> R.color.mat6
    "coral" -> R.color.mat8
    "hot_pink" -> R.color.mat2
    else -> R.color.mat2
}

fun mapIconNameToIconResId(name: String) = when (name) {
    "groceries" -> R.drawable.ic_shopping_cart_black_24dp
    "gym" -> R.drawable.ic_pool_black_24dp
    "food" -> R.drawable.ic_food
    "beauty" -> R.drawable.ic_beauty
    "social" -> R.drawable.ic_social
    "wallet" -> R.drawable.ic_account_balance_wallet_24dp
    else -> R.drawable.ic_pie_chart_white_24dp
}