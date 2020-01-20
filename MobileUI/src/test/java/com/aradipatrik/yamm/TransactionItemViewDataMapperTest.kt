package com.aradipatrik.yamm

import com.aradipatrik.yamm.features.history.mapper.IconMapper
import com.aradipatrik.yamm.features.history.mapper.TransactionViewDataMapper
import com.aradipatrik.yamm.features.history.model.TransactionItemViewData
import com.aradipatrik.yamm.util.PresentationLayerMocks.transactionPresentation
import org.junit.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class TransactionItemViewDataMapperTest {
    private val iconMapper = IconMapper()
    private val transactionViewDataMapper = TransactionViewDataMapper(iconMapper)

    @Test
    fun `mapToViewData should work`() {
        val p = transactionPresentation()
        val expectedViewData = TransactionItemViewData(
            memo = p.memo,
            categoryIconResId = iconMapper.mapToResource(p.category.iconId),
            amount = p.amount,
            colorResId = 0,
            categoryName = p.category.name,
            presentationRef = p
        )
        transactionViewDataMapper.mapToItemViewData(p)
        expectThat(transactionViewDataMapper.mapToItemViewData(p)).isEqualTo(expectedViewData)
    }
}