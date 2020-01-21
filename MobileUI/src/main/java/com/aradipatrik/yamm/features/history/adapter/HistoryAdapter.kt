package com.aradipatrik.yamm.features.history.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.aradipatrik.yamm.features.history.model.TransactionItemViewData

object TransactionViewDataItemCallback : DiffUtil.ItemCallback<TransactionItemViewData>() {
    override fun areItemsTheSame(
        oldItem: TransactionItemViewData,
        newItem: TransactionItemViewData
    ) = oldItem.presentationRef.id == newItem.presentationRef.id

    override fun areContentsTheSame(
        oldItem: TransactionItemViewData,
        newItem: TransactionItemViewData
    ) = oldItem == newItem
}

class HistoryAdapter :
    ListAdapter<TransactionItemViewData, HistoryAdapter.ViewHolder>(
        TransactionViewDataItemCallback
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
}
