package com.aradipatrik.yamm.features.addtransaction.mapper

import com.aradipatrik.presentation.presentations.CategoryPresentationModel
import com.aradipatrik.yamm.common.mapper.IconMapper
import com.aradipatrik.yamm.features.addtransaction.model.CategoryItemViewData

class CategoryItemViewDataMapper(private val iconMapper: IconMapper) {
    fun mapToItemViewData(categoryPresentationModel: CategoryPresentationModel, isSelected: Boolean) =
        CategoryItemViewData(
            presentationModelRef = categoryPresentationModel,
            isSelected = isSelected,
            categoryName = categoryPresentationModel.name,
            iconResId = iconMapper.mapToResource(categoryPresentationModel.iconId),
            colorResId = 0
        )
}
