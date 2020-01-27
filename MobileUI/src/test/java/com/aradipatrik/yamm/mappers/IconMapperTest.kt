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
        expectThat(iconMapper.mapToResource("groceries"))
            .isEqualTo(R.drawable.ic_shopping_cart_black_24dp)
    }
}