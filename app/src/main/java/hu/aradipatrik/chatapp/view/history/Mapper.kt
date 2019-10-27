package hu.aradipatrik.chatapp.view.history

import hu.aradipatrik.chatapp.R
import hu.aradipatrik.chatapp.model.Transaction
import org.joda.time.DateTime
import java.util.*

fun mapCategorisedTransactionsToItems(
    categorisedTransactions: TreeMap<DateTime, TreeSet<Transaction>>
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
            it.amount
        )
    })

    historyItems
}

fun mapColorNameToColorResId(name: String) = when (name) {
    "orange" -> R.color.orange
    "light_salmon" -> R.color.light_salmon
    "dark_orange" -> R.color.dark_orange
    "coral" -> R.color.coral
    "hot_pink" -> R.color.hot_pink
    else -> R.color.dark_khaki
}

fun mapIconNameToIconResId(name: String) = when (name) {
    "groceries" -> R.drawable.ic_shopping_cart_black_24dp
    "gym" -> R.drawable.ic_pool_black_24dp
    "food" -> R.drawable.ic_food
    "beauty" -> R.drawable.ic_beauty
    "social" -> R.drawable.ic_social
    else -> R.drawable.ic_pie_chart_white_24dp
}