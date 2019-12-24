package com.aradipatrik.data.mapper

import com.aradipatrik.data.model.CategoryEntity
import com.aradipatrik.domain.model.Category
import javax.inject.Inject

class CategoryMapper @Inject constructor() : EntityMapper<CategoryEntity, Category> {
    override fun mapFromEntity(entity: CategoryEntity) =
        Category(entity.id, entity.name, entity.iconId)

    override fun mapToEntity(domain: Category) = CategoryEntity(
        domain.id, domain.name, domain.iconId,
        lastUpdateTimestamp = TimestampProvider.now()
    )
}