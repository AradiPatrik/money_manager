package com.aradipatrik.data.mapper

import com.aradipatrik.data.model.CategoryEntity
import com.aradipatrik.domain.model.Category

class CategoryMapper {
    fun mapFromEntity(entity: CategoryEntity) =
        Category(entity.id, entity.name, entity.iconId)

    fun mapToEntity(domain: Category) = CategoryEntity(
        id = domain.id, name = domain.name, iconId = domain.iconId, walletId = "",
        updatedTimeStamp = TimestampProvider.now(), syncStatus = SyncStatus.None
    )
}
