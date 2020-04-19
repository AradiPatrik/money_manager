package com.aradipatrik.yamm.mappers

import com.aradipatrik.presentation.presentations.CategoryPresentationModel
import com.aradipatrik.yamm.common.mapper.IconMapper
import com.aradipatrik.yamm.features.addtransaction.mapper.CategoryItemViewDataMapper
import org.junit.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class CategoryItemViewDataMapperTest {
    @Test
    fun `CategoryItemViewDataMapper should map CategoryPresentation data correctly, and it should set selected to false`() {
        // Arrange
        val iconMapper = IconMapper()
        val mapper = CategoryItemViewDataMapper(iconMapper)
        val presentation = CategoryPresentationModel(
            id = "testId",
            name = "testName",
            iconId = "Groceries"
        )
        // Act
        val viewData = mapper.mapToItemViewData(presentation, true)

        // Assert
        expectThat(viewData.presentationModelRef).isEqualTo(presentation)
        expectThat(viewData.categoryName).isEqualTo("testName")
        expectThat(viewData.iconResId).isEqualTo(iconMapper.mapToResource(presentation.iconId))
        expectThat(viewData.colorResId).isEqualTo(0)
        expectThat(viewData.isSelected).isEqualTo(true)
    }
}