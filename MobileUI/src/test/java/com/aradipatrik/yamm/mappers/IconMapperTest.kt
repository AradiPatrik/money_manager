package com.aradipatrik.yamm.mappers

import com.aradipatrik.yamm.R
import com.aradipatrik.yamm.common.mapper.IconMapper
import org.junit.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class IconMapperTest {
    private val iconMapper = IconMapper()

    @Test
    fun `mapToResource should work`() {
        validateMappings(mapOf(
            "Groceries" to R.drawable.category_icons_shopping_cart,
            "Sports" to R.drawable.category_icons_dumbbell,
            "Food" to R.drawable.category_icons_pizza_slice,
            "Gift" to R.drawable.category_icons_gift,
            "Rent" to R.drawable.category_icons_home,
            "Health" to R.drawable.category_icons_first_aid,
            "Entertainment" to R.drawable.category_icons_gamepad,
            "Transportation" to R.drawable.category_icons_subway
        ))
    }

    private fun validateMappings(map: Map<String, Int>) {
        map.entries.forEach { (key, value) ->
            expectThat(iconMapper.mapToResource(key)).isEqualTo(value)
        }
    }
}