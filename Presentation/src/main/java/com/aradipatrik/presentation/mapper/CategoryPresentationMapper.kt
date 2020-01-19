package com.aradipatrik.presentation.mapper

import com.aradipatrik.domain.model.Category
import com.aradipatrik.presentation.presentations.CategoryPresentation
import javax.inject.Inject

class CategoryPresentationMapper {
    fun mapToPresentation(category: Category) =
        CategoryPresentation(
            id = category.id,
            iconId = category.iconId,
            name = category.name
        )

    fun mapFromPresentation(presentation: CategoryPresentation) = Category(
        id = presentation.id,
        iconId = presentation.iconId,
        name = presentation.name
    )
}