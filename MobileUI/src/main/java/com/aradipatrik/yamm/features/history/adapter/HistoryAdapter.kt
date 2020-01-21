package com.aradipatrik.yamm.features.history.adapter

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.aradipatrik.yamm.features.history.model.TransactionHeaderViewData
import com.aradipatrik.yamm.features.history.model.TransactionItemViewData
import com.aradipatrik.yamm.features.history.model.TransactionViewData
import kotlinx.android.synthetic.main.list_item_history_transaction.view.*

object TransactionViewDataItemCallback : DiffUtil.ItemCallback<TransactionViewData>() {
    override fun areItemsTheSame(
        oldItem: TransactionViewData,
        newItem: TransactionViewData
    ) = when {
        oldItem is TransactionItemViewData && newItem is TransactionItemViewData ->
            oldItem.presentationRef.id == newItem.presentationRef.id
        oldItem is TransactionHeaderViewData && newItem is TransactionHeaderViewData ->
            oldItem.localDateRef == newItem.localDateRef
        else -> false
    }

    // It's okay to suppress DiffUtilEquals lint here, because it produces a false positive,
    // saying that TransactionViewData class don't have a proper equals method.
    // We are only checking equality between data classes with the same types.
    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(
        oldItem: TransactionViewData,
        newItem: TransactionViewData
    ) = when {
        oldItem is TransactionItemViewData && newItem is TransactionItemViewData ->
            oldItem == newItem
        oldItem is TransactionHeaderViewData && newItem is TransactionHeaderViewData ->
            oldItem == newItem
        else -> false
    }
}

class HistoryAdapter
    : ListAdapter<TransactionViewData, HistoryAdapter.ViewHolder>(TransactionViewDataItemCallback) {
    companion object {
        const val TRANSACTION_HEADER = 0
        const val TRANSACTION_ITEM = 1
    }

    override fun getItemViewType(position: Int) = when (currentList[position]) {
        is TransactionHeaderViewData -> TRANSACTION_HEADER
        is TransactionItemViewData -> TRANSACTION_ITEM
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    sealed class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        class TransactionItemViewHolder(view: View) : ViewHolder(view) {
            private val circularBackground = view.circular_background
            private val categoryIcon = view.category_icon
            private val categoryName = view.category_name
            private val memo = view.memo
            private val amount = view.amount

            fun bind(transactionItemViewData: TransactionItemViewData) {

            }
        }
        class HeaderItemViewHolder(view: View) : ViewHolder(view)
    }
}
