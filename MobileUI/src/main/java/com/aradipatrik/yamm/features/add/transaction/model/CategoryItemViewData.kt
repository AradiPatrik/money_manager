package com.aradipatrik.yamm.features.add.transaction.model

import com.aradipatrik.presentation.presentations.CategoryPresentationModel

data class CategoryItemViewData(
    val presentationModelRef: CategoryPresentationModel,
    val categoryName: String,
    val iconResId: Int,
    val colorResId: Int,
    val isSelected: Boolean
)
