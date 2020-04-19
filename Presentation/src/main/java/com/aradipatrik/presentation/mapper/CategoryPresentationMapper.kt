package com.aradipatrik.presentation.mapper

import com.aradipatrik.domain.model.Category
import com.aradipatrik.presentation.presentations.CategoryPresentationModel

class CategoryPresentationMapper {
    fun mapToPresentation(category: Category) =
        CategoryPresentationModel(
            id = category.id,
            iconId = category.iconId,
            name = category.name
        )

    fun mapFromPresentation(presentationModel: CategoryPresentationModel) = Category(
        id = presentationModel.id,
        iconId = presentationModel.iconId,
        name = presentationModel.name
    )
}
