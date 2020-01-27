package com.aradipatrik.yamm.features.add.transaction.model

import com.aradipatrik.presentation.presentations.CategoryPresentation

data class CategoryItemViewData(
    val presentationRef: CategoryPresentation,
    val categoryName: String,
    val iconResId: Int,
    val colorResId: Int,
    val isSelected: Boolean
)
