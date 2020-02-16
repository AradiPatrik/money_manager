package com.aradipatrik.yamm.common.mapper

import com.aradipatrik.yamm.R

class IconMapper {
    companion object {
        private val mappings = hashMapOf(
            "Groceries" to R.drawable.category_icons_shopping_cart,
            "Food" to R.drawable.category_icons_pizza_slice,
            "Gift" to R.drawable.category_icons_gift,
            "Rent" to R.drawable.category_icons_home,
            "Entertainment" to R.drawable.category_icons_gamepad,
            "Health" to R.drawable.category_icons_first_aid,
            "Transportation" to R.drawable.category_icons_subway,
            "Sports" to R.drawable.category_icons_dumbbell
        )
    }

    fun mapToResource(s: String) = mappings[s]!!

}