package com.aradipatrik.yamm.features.add.transaction.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.aradipatrik.yamm.R
import com.aradipatrik.yamm.common.viewext.inflate
import com.aradipatrik.yamm.features.add.transaction.adapter.CategoryAdapter.ViewHolder
import com.aradipatrik.yamm.features.add.transaction.model.CategoryItemViewData
import kotlinx.android.synthetic.main.list_item_category.view.*

object CategoryItemViewDataItemCallback : DiffUtil.ItemCallback<CategoryItemViewData>() {
    override fun areItemsTheSame(
        oldItem: CategoryItemViewData,
        newItem: CategoryItemViewData
    ) = oldItem.presentationRef.id == newItem.presentationRef.id

    override fun areContentsTheSame(
        oldItem: CategoryItemViewData,
        newItem: CategoryItemViewData
    ) = oldItem.presentationRef == newItem.presentationRef
            && oldItem.isSelected == newItem.isSelected
}

class CategoryAdapter :
    ListAdapter<CategoryItemViewData, ViewHolder>(CategoryItemViewDataItemCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        parent.inflate(R.layout.list_item_category)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val icon = view.category_icon_image_view
        private val name = view.category_name_text_view

        fun bind(categoryItemViewData: CategoryItemViewData) {
            icon.setImageResource(categoryItemViewData.iconResId)
            name.text = categoryItemViewData.categoryName
        }
    }
}