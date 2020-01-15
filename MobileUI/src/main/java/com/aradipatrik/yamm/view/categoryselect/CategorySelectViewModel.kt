package com.aradipatrik.yamm.view.categoryselect

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aradipatrik.yamm.R
import javax.inject.Inject

class CategorySelectViewModel @Inject constructor() : ViewModel() {
    val allCategories = MutableLiveData(
            listOf(
                    CategoryItem("Food", R.drawable.ic_food, false),
                    CategoryItem("Beauty", R.drawable.ic_beauty, false),
                    CategoryItem("Groceries", R.drawable.ic_shopping_cart_black_24dp, false),
                    CategoryItem("Calendar", R.drawable.ic_date_range_24dp, false),
                    CategoryItem("Training", R.drawable.ic_social, false),
                    CategoryItem("Food", R.drawable.ic_food, false),
                    CategoryItem("Beauty", R.drawable.ic_beauty, false),
                    CategoryItem("Groceries", R.drawable.ic_shopping_cart_black_24dp, false),
                    CategoryItem("Calendar", R.drawable.ic_date_range_24dp, false),
                    CategoryItem("Training", R.drawable.ic_social, false),
                    CategoryItem("Food", R.drawable.ic_food, false),
                    CategoryItem("Beauty", R.drawable.ic_beauty, false),
                    CategoryItem("Groceries", R.drawable.ic_shopping_cart_black_24dp, true),
                    CategoryItem("Calendar", R.drawable.ic_date_range_24dp, false),
                    CategoryItem("Training", R.drawable.ic_social, false),
                    CategoryItem("Food", R.drawable.ic_food, false),
                    CategoryItem("Beauty", R.drawable.ic_beauty, false),
                    CategoryItem("Groceries", R.drawable.ic_shopping_cart_black_24dp, false),
                    CategoryItem("Calendar", R.drawable.ic_date_range_24dp, false),
                    CategoryItem("Training", R.drawable.ic_social, false),
                    CategoryItem("Food", R.drawable.ic_food, false),
                    CategoryItem("Beauty", R.drawable.ic_beauty, false),
                    CategoryItem("Groceries", R.drawable.ic_shopping_cart_black_24dp, false),
                    CategoryItem("Calendar", R.drawable.ic_date_range_24dp, false),
                    CategoryItem("Training", R.drawable.ic_social, false),
                    CategoryItem("Food", R.drawable.ic_food, false),
                    CategoryItem("Beauty", R.drawable.ic_beauty, false),
                    CategoryItem("Groceries", R.drawable.ic_shopping_cart_black_24dp, false),
                    CategoryItem("Calendar", R.drawable.ic_date_range_24dp, false),
                    CategoryItem("Training", R.drawable.ic_social, false)
            )
    )
}
