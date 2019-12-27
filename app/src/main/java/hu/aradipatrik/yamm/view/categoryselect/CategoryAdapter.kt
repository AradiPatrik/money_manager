package hu.aradipatrik.yamm.view.categoryselect

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import hu.aradipatrik.yamm.R
import hu.aradipatrik.yamm.databinding.ListItemCategoryBinding

class CategoryItem(
        val categoryName: String,
        val iconResId: Int,
        val isSelected: Boolean
)

object CategoryItemDiffUtilCallback : DiffUtil.ItemCallback<CategoryItem>() {
    override fun areItemsTheSame(oldItem: CategoryItem, newItem: CategoryItem) =
            oldItem.categoryName == newItem.categoryName

    override fun areContentsTheSame(
            oldItem: CategoryItem,
            newItem: CategoryItem
    ) =
            oldItem.iconResId == newItem.iconResId &&
                    oldItem.isSelected == newItem.isSelected
}

class CategoryListAdapter(private val onSelect: (categoryName: String) -> Unit) :
        ListAdapter<CategoryItem, IconItemViewHolder>(CategoryItemDiffUtilCallback) {
    override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
    ) = IconItemViewHolder(
            ListItemCategoryBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
            ), onSelect = onSelect
    )

    override fun onBindViewHolder(holder: IconItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}

class IconItemViewHolder(
        val binding: ListItemCategoryBinding,
        val onSelect: (categoryName: String) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: CategoryItem) {
        binding.iconResId = item.iconResId
        binding.name = item.categoryName
        binding.tintResId = if (item.isSelected)
            R.color.primaryColor
        else
            R.color.defaultForegroundColor
        binding.listItemCategoryContainer.setOnClickListener {
            onSelect(item.categoryName)
        }
        binding.executePendingBindings()
    }
}