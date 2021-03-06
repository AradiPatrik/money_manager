package com.aradipatrik.yamm.mappers

import com.aradipatrik.yamm.common.mapper.IconMapper
import com.aradipatrik.yamm.features.history.mapper.TransactionViewDataMapper
import com.aradipatrik.yamm.features.history.model.TransactionHeaderViewData
import com.aradipatrik.yamm.features.history.model.TransactionItemViewData
import com.aradipatrik.yamm.util.PresentationLayerMocks.categoryPresentation
import com.aradipatrik.yamm.util.PresentationLayerMocks.transactionPresentation
import org.joda.time.LocalDate
import org.junit.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class TransactionItemViewDataMapperTest {
    private val iconMapper = IconMapper()
    private val transactionViewDataMapper = TransactionViewDataMapper(iconMapper)

    @Test
    fun `mapToItemViewData should work`() {
        val p =
            transactionPresentation(categoryPresentationModel = categoryPresentation(iconId = "Groceries"))
        val expectedViewData = TransactionItemViewData(
            memo = p.memo,
            categoryIconResId = iconMapper.mapToResource(p.categoryModel.iconId),
            amount = p.amount,
            colorResId = 0,
            categoryName = p.categoryModel.name,
            presentationModelRef = p
        )
        expectThat(transactionViewDataMapper.mapToItemViewData(p)).isEqualTo(expectedViewData)
    }

    @Test
    fun `mapToHeaderViewData should work`() {
        val expectedViewData = TransactionHeaderViewData(LocalDate(2019, 1, 28))
        val res = transactionViewDataMapper.mapToHeaderViewData(LocalDate(2019, 1, 28))
        expectThat(res).isEqualTo(expectedViewData)
    }
}