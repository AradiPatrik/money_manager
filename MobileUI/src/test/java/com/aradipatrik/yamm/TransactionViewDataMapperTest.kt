package com.aradipatrik.yamm

import com.aradipatrik.yamm.mapper.IconMapper
import com.aradipatrik.yamm.mapper.TransactionViewDataMapper
import com.aradipatrik.yamm.model.TransactionViewData
import com.aradipatrik.yamm.util.PresentationLayerMocks.transactionPresentation
import org.junit.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class TransactionViewDataMapperTest {
    private val iconMapper = IconMapper()
    private val transactionViewDataMapper = TransactionViewDataMapper(iconMapper)

    @Test
    fun `mapToViewData should work`() {
        val p = transactionPresentation()
        val expectedViewData = TransactionViewData(
            id = p.id,
            categoryId = p.category.id,
            memo = p.memo,
            categoryIconResId = iconMapper.mapToResource(p.category.iconId),
            amount = p.amount,
            colorResId = 0,
            categoryName = p.category.name
        )
        transactionViewDataMapper.mapToViewData(p)
        expectThat(transactionViewDataMapper.mapToViewData(p)).isEqualTo(expectedViewData)
    }
}