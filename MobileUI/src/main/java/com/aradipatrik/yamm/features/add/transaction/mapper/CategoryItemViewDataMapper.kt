package com.aradipatrik.yamm.features.add.transaction.mapper

import com.aradipatrik.presentation.presentations.CategoryPresentation
import com.aradipatrik.yamm.common.mapper.IconMapper
import com.aradipatrik.yamm.features.add.transaction.model.CategoryItemViewData

class CategoryItemViewDataMapper(private val iconMapper: IconMapper) {
    fun mapToItemViewData(categoryPresentation: CategoryPresentation, isSelected: Boolean) = CategoryItemViewData(
        presentationRef = categoryPresentation,
        isSelected = isSelected,
        categoryName = categoryPresentation.name,
        iconResId = iconMapper.mapToResource(categoryPresentation.iconId),
        colorResId = 0
    )
}
