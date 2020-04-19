package com.aradipatrik.yamm.itemcallback

import com.aradipatrik.testing.CommonMocks.string
import com.aradipatrik.yamm.features.addtransaction.view.CategoryItemViewDataItemCallback
import com.aradipatrik.yamm.util.PresentationLayerMocks.categoryPresentation
import com.aradipatrik.yamm.util.ViewDataMocks.categoryViewData
import org.junit.Test
import strikt.api.expectThat
import strikt.assertions.isFalse
import strikt.assertions.isTrue

class CategoryItemViewDataItemCallbackTest {
    @Test
    fun `Items should be the same if presentation ids are the same`() {
        // Arrange
        val presentation = categoryPresentation()
        val item = categoryViewData(presentationModelRef = presentation)
        val sameItem = categoryViewData(presentationModelRef = presentation)
        val idDifferent = categoryViewData(presentationModelRef = presentation.copy(id = string()))

        // Act
        val itemSameResult = CategoryItemViewDataItemCallback.areItemsTheSame(item, sameItem)
        val idDifferentResult = CategoryItemViewDataItemCallback.areItemsTheSame(item, idDifferent)

        // Assert
        expectThat(itemSameResult).isTrue()
        expectThat(idDifferentResult).isFalse()
    }

    @Test
    fun `Items contents should be the same if presentation contents are the same, and selection state is the same`() {
        // Arrange
        val presentation = categoryPresentation()
        val item = categoryViewData(presentation, isSelected = true)
        val sameContentItem = categoryViewData(presentation, isSelected = true)

        // Act
        val result = CategoryItemViewDataItemCallback.areContentsTheSame(item, sameContentItem)

        // Assert
        expectThat(result).isTrue()
    }

    @Test
    fun `Items contents should be different if presentations are different`() {
        // Arrange
        val item = categoryViewData(categoryPresentation(), isSelected = true)
        val notSameContentItem = categoryViewData(categoryPresentation(), isSelected = true)

        // Act
        val result = CategoryItemViewDataItemCallback.areContentsTheSame(item, notSameContentItem)

        // Assert
        expectThat(result).isFalse()
    }

    @Test
    fun `Items contents should be different if selection state is different`() {
        // Arrange
        val presentation = categoryPresentation()
        val item = categoryViewData(presentation, isSelected = false)
        val notSameContentItem = categoryViewData(presentation, isSelected = true)

        // Act
        val result = CategoryItemViewDataItemCallback.areContentsTheSame(item, notSameContentItem)

        // Assert
        expectThat(result).isFalse()
    }
}
