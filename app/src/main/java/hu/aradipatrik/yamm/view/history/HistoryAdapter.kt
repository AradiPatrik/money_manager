package hu.aradipatrik.yamm.view.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import hu.aradipatrik.yamm.databinding.ListItemHistoryHeaderBinding
import hu.aradipatrik.yamm.databinding.ListItemHistoryTransactionBinding
import hu.aradipatrik.yamm.util.isSameDay
import hu.aradipatrik.yamm.view.history.HistoryItem.Companion.HEADER_TYPE_ID
import hu.aradipatrik.yamm.view.history.HistoryItem.Companion.TRANSACTION_TYPE_ID
import hu.aradipatrik.yamm.view.history.HistoryItem.HeaderItem
import hu.aradipatrik.yamm.view.history.HistoryItem.TransactionItem
import org.joda.time.DateTime

sealed class HistoryItem(val typeId: Int) {
    companion object {
        const val HEADER_TYPE_ID = 0
        const val TRANSACTION_TYPE_ID = 1
    }

    object DiffCallback : DiffUtil.ItemCallback<HistoryItem>() {
        override fun areItemsTheSame(oldItem: HistoryItem, newItem: HistoryItem) =
                when {
                    oldItem is HeaderItem && newItem is HeaderItem ->
                        oldItem.date.isSameDay(newItem.date)
                    oldItem is TransactionItem && newItem is TransactionItem ->
                        oldItem.uid == newItem.uid
                    else -> false
                }

        override fun areContentsTheSame(
                oldItem: HistoryItem,
                newItem: HistoryItem
        ) =
                when {
                    oldItem is HeaderItem && newItem is HeaderItem -> areItemsTheSame(
                            oldItem,
                            newItem
                    )
                    oldItem is TransactionItem && newItem is TransactionItem ->
                        oldItem.iconResId == newItem.iconResId &&
                                oldItem.colorResId == newItem.colorResId &&
                                oldItem.categoryName == newItem.categoryName &&
                                oldItem.memo == newItem.memo &&
                                oldItem.amount == newItem.amount
                    else -> throw IllegalStateException(
                            "items the same returned true for items: ($oldItem, $newItem)"
                    )
                }

    }

    data class HeaderItem(val date: DateTime) : HistoryItem(HEADER_TYPE_ID)
    data class TransactionItem(
            val uid: Int,
            val iconResId: Int,
            val colorResId: Int,
            val categoryName: String,
            val memo: String,
            val amount: Int
    ) : HistoryItem(TRANSACTION_TYPE_ID)
}

class HistoryAdapter :
        ListAdapter<HistoryItem, HistoryViewHolder>(HistoryItem.DiffCallback) {

    override fun getItemViewType(position: Int) = getItem(position).typeId

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            when (viewType) {
                HEADER_TYPE_ID -> createHeaderViewHolder(parent)
                TRANSACTION_TYPE_ID -> createItemViewHolder(parent)
                else -> throw IllegalStateException(
                        "View type of: $viewType did not match any HistoryItem view type"
                )
            }

    private fun createHeaderViewHolder(parent: ViewGroup) =
            HeaderItemViewHolder(
                    ListItemHistoryHeaderBinding.inflate(
                            LayoutInflater.from(parent.context),
                            parent,
                            false
                    )
            )

    private fun createItemViewHolder(parent: ViewGroup) =
            TransactionItemViewHolder(
                    ListItemHistoryTransactionBinding.inflate(
                            LayoutInflater.from(parent.context),
                            parent,
                            false
                    )
            )

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) =
            holder.bind(getItem(position))
}

sealed class HistoryViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
    abstract fun bind(item: HistoryItem)
}

class TransactionItemViewHolder(val binding: ListItemHistoryTransactionBinding) :
        HistoryViewHolder(binding.root) {
    override fun bind(item: HistoryItem) {
        require(item is TransactionItem) {
            "Tried to bind header item to transaction item view holder"
        }
        binding.transaction = item
        binding.executePendingBindings()
    }
}

class HeaderItemViewHolder(val binding: ListItemHistoryHeaderBinding) :
        HistoryViewHolder(binding.root) {
    override fun bind(item: HistoryItem) {
        require(item is HeaderItem) {
            "Tried to bind transaction item to header item view holder"
        }
        binding.title = item.date.toString("YYYY, MMMMM, dd")
        binding.executePendingBindings()
    }
}
